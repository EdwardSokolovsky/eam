package repo

import properties.GeneralProperties._
import collection.JavaConverters._
import com.therandomlabs.curseapi.CurseAPI
import com.therandomlabs.curseapi.game.{CurseCategory, CurseCategorySection}
import com.therandomlabs.curseapi.project.{CurseProject, CurseSearchQuery}

object AddonsFinder {

  def findAddon(nameOrPartName: String): List[CurseProject] = {
    val projectBuffer = scala.collection.mutable.ListBuffer[CurseProject]()
    val ccs: Set[CurseCategorySection] = CurseAPI.game(1).get().categorySections().asScala.toSet
    val addonsCategories = ccs.map(section => section.categories()).head.asScala.toSet
    addonsCategories.foreach(c => {
      val sectionId = c.sectionID()
      val query = new CurseSearchQuery().gameID(wowGameId).categorySectionID(sectionId)
      val filter = query.searchFilter(nameOrPartName)
      val result = CurseAPI.searchProjects(filter)
      projectBuffer.++=(result.get.asScala.toList)
    })
    projectBuffer.toList
  }

}
