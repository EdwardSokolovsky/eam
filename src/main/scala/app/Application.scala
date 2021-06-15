package app

import javax.swing.{JFileChooser, JFrame, JOptionPane, JTextArea}
import consts.Messages._
import okhttp3.HttpUrl

import scala.swing._
import scala.swing.event.ButtonClicked
import properties.GeneralProperties._
import utils.{FileOperator, Updater}
import scala.util.{Failure, Success, Try}

class Application extends SimpleSwingApplication {

  private var addonsMap: Map[String, HttpUrl] = _

  def top: MainFrame = new MainFrame {

    title = s"$appName v.$appVersion"

    object changeAddonsFolder extends Button {
      text = "Select addon's folder"
    }

    object refreshBtn extends Button {
      text = "Refresh"
    }

    object updateAllBtn extends Button {
      text = "Update All"
    }

    object addonsFolder extends TextArea {
      this.editable = false
      columns = 30
    }

    object addonsList extends TextArea {
      this.editable = false
      this.text = defaultInfo
    }

    object exitButton extends Button {
      text = "Exit"
    }

    contents = new BoxPanel(scala.swing.Orientation.Vertical) {
      contents += new Label("Addon's folder:")
      contents += new ScrollPane(addonsFolder)
      contents += changeAddonsFolder
      contents += new Label("Addon's list:")
      contents += new ScrollPane(addonsList)
      contents += refreshBtn
      contents += updateAllBtn
      contents += exitButton
      border = Swing.EmptyBorder(15, 10, 10, 10)
    }

    listenTo(changeAddonsFolder, refreshBtn, exitButton)

    changeAddonsFolder.reactions += {
      case ButtonClicked(`changeAddonsFolder`) =>
        var result = selectAddonsFolder
        if (result.nonEmpty) addonsFolder.text = result
    }
    refreshBtn.reactions += {
      case ButtonClicked(`refreshBtn`) =>
        if (addonsFolder.text.nonEmpty) {
          if (FileOperator.exists(addonsFolder.text)) {
            val result = Try(addonsList.text = checkAddons(addonsFolder.text))
            result match {
              case Success(s) => println("Refrash successful!")
              case Failure(f) =>
                JOptionPane.showMessageDialog(
                  new JFrame(), somethingWrongMsg, "Error!", JOptionPane.ERROR_MESSAGE
                )
            }
          } else {
            JOptionPane.showMessageDialog(
              new JFrame(), pathDoesnotExistsMsg(addonsFolder.text), "Error!", JOptionPane.ERROR_MESSAGE
            )
          }
        }
    }
    updateAllBtn.reactions += {
      case ButtonClicked(`updateAllBtn`) =>
        if (addonsFolder.text.nonEmpty) {
          if (FileOperator.exists(addonsFolder.text)) {
            val result = updateAll(addonsFolder.text, addonsMap)
            if (result._2){
              JOptionPane.showMessageDialog(
                new JFrame(), doneMsg, "Info!", JOptionPane.INFORMATION_MESSAGE
              )
            } else {
              JOptionPane.showMessageDialog(
                new JFrame(), result._1, "Error!", JOptionPane.ERROR_MESSAGE
              )
            }
          } else {
            JOptionPane.showMessageDialog(
              new JFrame(), pathDoesnotExistsMsg(addonsFolder.text), "Warning!", JOptionPane.ERROR_MESSAGE
            )
          }
        }
    }
    exitButton.reactions += {
      case ButtonClicked(`exitButton`) =>
        sys.exit(0)
    }
  }

  protected def selectAddonsFolder: String = {
    var result = ""
    val fileChooser = new JFileChooser()
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
    val parentFrame = new JFrame()
    val file = fileChooser.showOpenDialog(parentFrame)
    if (file == JFileChooser.APPROVE_OPTION) {
      result = fileChooser.getSelectedFile.getPath
    }
    result
  }

  protected def checkAddons(addonsFolder: String): String = {
    val updater = new Updater(addonsFolder)
    val result = updater.checkInstalledAddons()
    addonsMap = result.filter(p => p._2._2).map(e => (e._1, e._2._1))
    result.map(addon => {
      s"Addon: ${addon._1}, need to update: ${addon._2._2}"
    }).mkString("\n")
  }

  protected def updateAll(
   addonsFolder: String,
   addonsToUpdate: Map[String, HttpUrl]
  ): (String, Boolean) = {
    val updater = new Updater(addonsFolder)
    val result = Try(updater.update(addonsToUpdate, addonsFolder))
    result match {
      case Success(s) => ("Success", true)
      case Failure(f) => (f.getMessage, false)
    }
  }

}