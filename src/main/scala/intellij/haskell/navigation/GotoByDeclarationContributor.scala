/*
 * Copyright 2016 Rik van der Kleij
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intellij.haskell.navigation

import com.intellij.navigation.{GotoClassContributor, NavigationItem}
import com.intellij.openapi.project.Project
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ArrayUtil
import intellij.haskell.psi.stubs.index.HaskellAllNameIndex
import intellij.haskell.psi.{HaskellNamedElement, HaskellPsiUtil}
import intellij.haskell.util.HaskellProjectUtil

import scala.collection.JavaConverters._

class GotoByDeclarationContributor extends GotoClassContributor {

  override def getNames(project: Project, includeNonProjectItems: Boolean): Array[String] = {
    ArrayUtil.toStringArray(StubIndex.getInstance.getAllKeys(HaskellAllNameIndex.Key, project))
  }

  override def getItemsByName(name: String, pattern: String, project: Project, includeNonProjectItems: Boolean): Array[NavigationItem] = {
    val searchScope = HaskellProjectUtil.getSearchScope(project, includeNonProjectItems)
    val namedElements = StubIndex.getElements(HaskellAllNameIndex.Key, name, project, searchScope, classOf[HaskellNamedElement])
    val declarations = namedElements.asScala.filter(ne => HaskellPsiUtil.findHighestDeclarationElementParent(ne).exists(_.getIdentifierElements.exists(_ == ne && name.toLowerCase.contains(pattern.toLowerCase))))
    declarations.toArray
  }

  override def getQualifiedNameSeparator: String = "."

  override def getQualifiedName(item: NavigationItem): String = null
}
