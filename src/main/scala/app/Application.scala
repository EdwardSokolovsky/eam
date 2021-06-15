package app

import javax.swing.{JFileChooser, JFrame, JOptionPane}
import consts.Messages._

import scala.swing._
import scala.swing.event.ButtonClicked
import properties.GeneralProperties._
import utils.{FileOperator, Updater}

import scala.util.{Failure, Success, Try}

class Application extends SimpleSwingApplication{

  def top: MainFrame = new MainFrame {

    title = s"$appName v.$appVersion"

    object changeAddonsFolder extends Button {
      text = "Select addon's folder"
    }
    object refreshBtn extends Button {
      text = "Refresh"
    }
    object addonsFolder extends TextField {
      columns = 30
    }
    object addonsList extends TextField {
      columns = 100
    }
    object exitButton extends Button {
      text = "Exit"
    }

    contents = new BoxPanel(scala.swing.Orientation.Vertical) {
      contents += new Label("Addon's folder:")
      contents += addonsFolder
      contents += changeAddonsFolder
      contents += new Label("Addon's list:")
      contents += addonsList
      contents += refreshBtn
      contents += exitButton
      border = Swing.EmptyBorder(15, 10, 10, 10)
    }

    listenTo(changeAddonsFolder, refreshBtn, exitButton)

    changeAddonsFolder.reactions += {
      case ButtonClicked(`changeAddonsFolder`) =>
        var result = selectAddonsFolder
        if(result.nonEmpty) addonsFolder.text = result
    }
    refreshBtn.reactions += {
      case ButtonClicked(`refreshBtn`) =>
        if(addonsFolder.text.nonEmpty){
          if (FileOperator.exists(addonsFolder.text)){
            val result = Try(addonsList.text = checkAddons(addonsFolder.text))
            result match {
              case Success(s) => println("Refrash successful!")
              case Failure(f) =>
                JOptionPane.showMessageDialog(
                  new JFrame(), pathDoesnotExistsMsg(f.getMessage), "Error!", JOptionPane.ERROR_MESSAGE
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
    updater.checkInstalledAddons().map(addon => {
      s"Addon: ${addon._1}, need to update: ${addon._2._2}"
    }).mkString("\n")
  }

}