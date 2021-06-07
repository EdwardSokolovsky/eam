package utils

import java.nio.file.{Files, Path}
import java.nio.file.attribute.{BasicFileAttributes, FileTime}
import java.time.{LocalDateTime, ZoneId}

object DateOperator {

  def convert(fileTime: FileTime): LocalDateTime = {
    LocalDateTime.ofInstant(fileTime.toInstant, ZoneId.systemDefault)
  }

  def getCreationTime(absPath: Path): LocalDateTime = {
    val localFileAtributes = Files.readAttributes(absPath, classOf[BasicFileAttributes])
    LocalDateTime.ofInstant(localFileAtributes.creationTime.toInstant, ZoneId.systemDefault)
  }

}
