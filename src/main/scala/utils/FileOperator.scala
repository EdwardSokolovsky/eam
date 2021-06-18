package utils

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import java.io.{File, FileOutputStream}
import java.nio.channels.Channels
import java.nio.file.{Files, Paths}
import java.nio.file.attribute.BasicFileAttributes

import scala.util.{Failure, Success, Try}
import java.time.LocalDateTime
import java.time.ZoneId

import okhttp3.HttpUrl
import org.springframework.util.FileSystemUtils

object FileOperator {

  def downloadFile(httpUrl: HttpUrl, destFileAbsPath: String): Unit = {
    val result = Try(
      new FileOutputStream(destFileAbsPath)
        .getChannel.transferFrom(Channels.newChannel(
        httpUrl.url.openStream()), 0, Long.MaxValue
      )
    )
    result match {
      case Success(s) => println(s"Download: '$destFileAbsPath'")
      case Failure(f) => throw new Exception(f.getMessage)
    }
  }

  def unzip(absPath: String, destFolder: String): Unit = {
    val zipFile = new ZipFile(absPath)
    val result = Try(zipFile.extractAll(destFolder))
    result match {
      case Success(s) => println("Install success!")
      case Failure(f) => throw new ZipException(f.getMessage)
    }
  }

  def removeFileOrFolder(absPath: String): Unit = {
    if(Files.exists(Paths.get(absPath))){
      FileSystemUtils.deleteRecursively(new File(absPath))
    }
  }

  def exists(absPath: String): Boolean = {
    Files.exists(Paths.get(absPath))
  }

  def mkdirRecursive(absPath: String): Unit = {
    val path = Paths.get(absPath)
    if(!Files.exists(path)){Files.createDirectories(Paths.get(absPath))}
  }

  def writeTextFile(text: String, absPath: String): Unit = {
    val path = Paths.get(absPath)
    Files.write(path,text.getBytes)
  }

  def getCreationDate(absPath: String): LocalDateTime = {
    val file = Paths.get(absPath)
    val fileAtributes = Files.readAttributes(file, classOf[BasicFileAttributes])
    LocalDateTime.ofInstant(fileAtributes.creationTime.toInstant, ZoneId.systemDefault)
  }

}
