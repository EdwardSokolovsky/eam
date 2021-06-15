package utils

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import java.io.FileOutputStream
import java.nio.channels.Channels
import java.nio.file.{Files, Paths}
import java.nio.file.attribute.BasicFileAttributes
import scala.util.{Failure, Success, Try}
import java.time.LocalDateTime
import java.time.ZoneId
import okhttp3.HttpUrl

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

  def removeFile(absPath: String): Unit = {
    val result = Try(Files.deleteIfExists(Paths.get(absPath)))
    result match {
      case Success(s) => println(s"File '$absPath' was remove!")
      case Failure(f) => throw new Exception(f.getMessage)
    }
  }

  def exists(absPath: String): Boolean = {
    Files.exists(Paths.get(absPath))
  }

  def getCreationDate(absPath: String): LocalDateTime = {
    val file = Paths.get(absPath)
    val fileAtributes = Files.readAttributes(file, classOf[BasicFileAttributes])
    LocalDateTime.ofInstant(fileAtributes.creationTime.toInstant, ZoneId.systemDefault)
  }

}
