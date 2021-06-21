package app

import javax.swing._
import consts.Messages._
import okhttp3.HttpUrl
import scala.swing._
import scala.swing.event.ButtonClicked
import properties.GeneralProperties._
import utils.{FileOperator, Updater}
import scala.util.{Failure, Success, Try}

class Application extends SimpleSwingApplication {

  private var addonsMap: Map[String, (HttpUrl, Boolean)] = _
  private val (topB, leftB, bottomB, rightB) = (15, 10, 10, 10)
  private val AddonsFolderLineSize = 30

  def top: MainFrame = new MainFrame {

    iconImage = toolkit.getImage(getClass.getClassLoader.getResource("logo.png").toURI.toURL)

    title = appName + " v." + appVersion

    object ChangeAddonsFolder extends Button {
      text = "Select addon's folder"
    }

    object RefreshBtn extends Button {
      text = "Refresh"
    }

    object UpdateAllBtn extends Button {
      text = "Update All"
    }

    object AddonsFolder extends TextArea {
      this.editable = false
      columns = AddonsFolderLineSize
    }

    object AddonsList extends TextArea {
      this.editable = false
      this.text = defaultInfo
    }

    object ExitButton extends Button {
      text = "Exit"
    }

    contents = new BoxPanel(scala.swing.Orientation.Vertical) {
      contents += new Label("Addon's folder:")
      contents += new ScrollPane(AddonsFolder)
      contents += ChangeAddonsFolder
      contents += new Label("Addon's list:")
      contents += new ScrollPane(AddonsList)
      contents += RefreshBtn
      contents += UpdateAllBtn
      contents += ExitButton
      border = Swing.EmptyBorder(topB, leftB, bottomB, rightB)
    }

    listenTo(ChangeAddonsFolder, RefreshBtn, ExitButton)

    ChangeAddonsFolder.reactions += {
      case ButtonClicked(ChangeAddonsFolder) =>
        var result = selectAddonsFolder
        if (result.nonEmpty) AddonsFolder.text = result
    }
    RefreshBtn.reactions += {
      case ButtonClicked(RefreshBtn) =>
        if (AddonsFolder.text.nonEmpty) {
          if (FileOperator.exists(AddonsFolder.text)) {
            val result = Try(AddonsList.text = checkAddons(AddonsFolder.text))
            result match {
              case Success(s) => println("Refresh successful!")
              case Failure(f) =>
                JOptionPane.showMessageDialog(
                  new JFrame(), somethingWrongMsg, errorWindowName, JOptionPane.ERROR_MESSAGE
                )
            }
          } else {
            JOptionPane.showMessageDialog(
              new JFrame(), pathDoesnotExistsMsg(AddonsFolder.text), errorWindowName, JOptionPane.ERROR_MESSAGE
            )
          }
        }
    }
    UpdateAllBtn.reactions += {
      case ButtonClicked(UpdateAllBtn) =>
        if (AddonsFolder.text.nonEmpty) {
          if (FileOperator.exists(AddonsFolder.text)) {
            val result = updateAll(AddonsFolder.text, addonsMap)
            if (result._2){
              JOptionPane.showMessageDialog(
                new JFrame(), doneMsg, infoWindowName, JOptionPane.INFORMATION_MESSAGE
              )
            } else {
              JOptionPane.showMessageDialog(
                new JFrame(), result._1, errorWindowName, JOptionPane.ERROR_MESSAGE
              )
            }
          } else {
            JOptionPane.showMessageDialog(
              new JFrame(), pathDoesnotExistsMsg(AddonsFolder.text), warningWindowName, JOptionPane.ERROR_MESSAGE
            )
          }
        }
    }
    ExitButton.reactions += {
      case ButtonClicked(ExitButton) =>
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
    addonsMap = result
    result.map(addon => {
      s"Addon: ${addon._1}, need to update: ${addon._2._2}"
    }).mkString("\n")
  }

  protected def updateAll(
   addonsFolder: String,
   addonsToUpdate: Map[String, (HttpUrl, Boolean)]
  ): (String, Boolean) = {
    val updater = new Updater(addonsFolder)
    val result = Try(updater.update(addonsToUpdate, addonsFolder))
    result match {
      case Success(s) => ("Success", true)
      case Failure(f) => (f.getMessage, false)
    }
  }

}
