package nl.rubensten.texifyidea.util

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import nl.rubensten.texifyidea.index.LatexCommandsIndex
import nl.rubensten.texifyidea.psi.*
import kotlin.reflect.KClass

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// PSI ELEMENT ///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Get the offset where the psi element ends.
 */
fun PsiElement.endOffset(): Int = textOffset + textLength

/**
 * @see [PsiTreeUtil.getChildrenOfType]
 */
fun <T : PsiElement> PsiElement.childrenOfType(clazz: KClass<T>): Collection<T> = PsiTreeUtil.findChildrenOfType(this, clazz.java)

/**
 * @see [PsiTreeUtil.getParentOfType]
 */
fun <T : PsiElement> PsiElement.parentOfType(clazz: KClass<T>): T? = PsiTreeUtil.getParentOfType(this, clazz.java)

/**
 * Checks if the psi element has a parent of a given class.
 */
fun <T : PsiElement> PsiElement.hasParent(clazz: KClass<T>): Boolean = parentOfType(clazz) != null

/**
 * Checks if the psi element is in math mode or not.
 *
 * @return `true` when the element is in math mode, `false` when the element is in no math mode.
 */
fun PsiElement.inMathMode(): Boolean = hasParent(LatexMathContent::class)

/**
 * @see LatexPsiUtil.getPreviousSiblingIgnoreWhitespace
 */
fun PsiElement.previousSiblingIgnoreWhitespace() = LatexPsiUtil.getPreviousSiblingIgnoreWhitespace(this)

/**
 * @see LatexPsiUtil.getNextSiblingIgnoreWhitespace
 */
fun PsiElement.nextSiblingIgnoreWhitespace() = LatexPsiUtil.getNextSiblingIgnoreWhitespace(this)

/**
 * Finds the next sibling of the element that has the given type.
 *
 * @return The first following sibling of the given type, or `null` when the sibling couldn't be found.
 */
fun <T : PsiElement> PsiElement.nextSiblingOfType(clazz: KClass<T>): T? {
    var sibling: PsiElement? = this
    while (sibling != null) {
        if (clazz.java.isAssignableFrom(sibling::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return sibling as T
        }

        sibling = sibling.nextSibling
    }

    return null
}

/**
 * Finds the previous sibling of the element that has the given type.
 *
 * @return The first previous sibling of the given type, or `null` when the sibling couldn't be found.
 */
fun <T : PsiElement> PsiElement.previousSiblingOfType(clazz: KClass<T>): T? {
    var sibling: PsiElement? = this
    while (sibling != null) {
        if (clazz.java.isAssignableFrom(sibling::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return sibling as T
        }

        sibling = sibling.prevSibling
    }

    return null
}

/**
 * @see LatexPsiUtil.getAllChildren
 */
fun PsiElement.allChildren(): List<PsiElement> = LatexPsiUtil.getAllChildren(this)

/**
 * @see LatexPsiUtil.getChildren
 */
fun PsiElement.allLatexChildren(): List<PsiElement> = LatexPsiUtil.getChildren(this)

/**
 * Finds the `generations`th parent of the psi element.
 */
fun PsiElement.grandparent(generations: Int): PsiElement? {
    var parent: PsiElement = this
    for (i in 1..generations) {
        parent = parent.parent ?: return null
    }
    return parent
}

/**
 * Checks if the psi element has a (grand) parent that matches the given predicate.
 */
fun PsiElement.hasParentMatching(maxDepth: Int, predicate: (PsiElement) -> Boolean): Boolean {
    var count = 0
    var parent = this.parent
    while (parent != null && parent !is PsiFile) {
        if (predicate(parent)) {
            return true
        }

        parent = parent.parent

        if (count++ > maxDepth) {
            return false
        }
    }

    return false
}

/**
 * Checks if the element is in a direct environment.
 *
 * This method does not take nested environments into account. Meaning that only the first parent environment counts.
 */
fun PsiElement.inDirectEnvironment(environmentName: String): Boolean = inDirectEnvironment(listOf(environmentName))

/**
 * Checks if the element is one of certain direct environments.
 *
 * This method does not take nested environments into account. Meaning that only the first parent environment counts.
 */
fun PsiElement.inDirectEnvironment(validNames: Collection<String>): Boolean {
    val environment = parentOfType(LatexEnvironment::class) ?: return false
    val nameText = environment.name() ?: return false
    return validNames.contains(nameText.text)
}

/**
 * Checks if the psi element is a child of `parent`.
 *
 * @return `true` when the element is a child of `parent`, `false` when the element is not a child of `parent` or when
 *          `parent` is `null`
 */
fun PsiElement.isChildOf(parent: PsiElement?): Boolean {
    if (parent == null) {
        return false
    }

    return hasParentMatching(1000) { it == parent }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// PSI FILE //////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Get the corresponding document of the PsiFile.
 */
fun PsiFile.document(): Document? = PsiDocumentManager.getInstance(project).getDocument(this)

/**
 * @see [LatexCommandsIndex.getIndexCommands]
 */
fun PsiFile.commandsInFile(): Collection<LatexCommands> = LatexCommandsIndex.getIndexCommands(this)

/**
 * @see [LatexCommandsIndex.getIndexCommandsInFileSet]
 */
fun PsiFile.commandsInFileSet(): Collection<LatexCommands> = LatexCommandsIndex.getIndexCommandsInFileSet(this)

/**
 * @see TexifyUtil.getFileRelativeTo
 */
fun PsiFile.fileRelativeTo(path: String): PsiFile? = TexifyUtil.getFileRelativeTo(this, path)

/**
 * @see TexifyUtil.findLabelsInFileSet
 */
fun PsiFile.labelsInFileSet(): Set<String> = TexifyUtil.findLabelsInFileSet(this)

/**
 * @see TexifyUtil.getReferencedFileSet
 */
fun PsiFile.referencedFiles(): Set<PsiFile> = TexifyUtil.getReferencedFileSet(this)

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// LATEX ELEMENTS ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @see TexifyUtil.getNextCommand
 */
fun LatexCommands.nextCommand(): LatexCommands? = TexifyUtil.getNextCommand(this)

/**
 * @see TexifyUtil.getForcedFirstRequiredParameterAsCommand
 */
fun LatexCommands.forcedFirstRequiredParameterAsCommand(): LatexCommands = TexifyUtil.getForcedFirstRequiredParameterAsCommand(this)

/**
 * @see TexifyUtil.isCommandKnown
 */
fun LatexCommands.isKnown(): Boolean = TexifyUtil.isCommandKnown(this)

/**
 * Get the environment name of a begin/end command.
 *
 * @param element
 *              Either a [LatexBeginCommand] or a [LatexEndCommand]
 */
private fun beginOrEndEnvironmentName(element: PsiElement): String? {
    val children = element.childrenOfType(LatexNormalText::class)
    if (children.isEmpty()) {
        return null
    }

    return children.first().text
}

/**
 * @see TexifyUtil.isEntryPoint
 */
fun LatexBeginCommand.isEntryPoint(): Boolean = TexifyUtil.isEntryPoint(this)

/**
 * Looks up the name of the environment in the required parameter.
 */
fun LatexEnvironment.name(): LatexNormalText? {
    val parameters = childrenOfType(LatexParameter::class)
    if (parameters.isEmpty()) {
        return null
    }

    val parameter = parameters.first()
    val texts = parameter.childrenOfType(LatexNormalText::class)
    if (texts.isEmpty()) {
        return null
    }

    return texts.first()
}

/**
 * Get the environment name of the begin command.
 */
fun LatexBeginCommand.environmentName(): String? = beginOrEndEnvironmentName(this)

/**
 * Finds the [LatexEndCommand] that matches the begin command.
 */
fun LatexBeginCommand.endCommand(): LatexEndCommand? = nextSiblingOfType(LatexEndCommand::class)

/**
 * Get the environment name of the end command.
 */
fun LatexEndCommand.environmentName(): String? = beginOrEndEnvironmentName(this)

/**
 * Finds the [LatexBeginCommand] that matches the end command.
 */
fun LatexEndCommand.beginCommand(): LatexBeginCommand? = previousSiblingOfType(LatexBeginCommand::class)