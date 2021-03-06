package nl.hannahsten.texifyidea.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import nl.hannahsten.texifyidea.lang.LatexRegularCommand
import nl.hannahsten.texifyidea.run.linuxpdfviewer.PdfViewer

/**
 * @author Sten Wessel
 */
@State(name = "TexifySettings", storages = [(Storage("texifySettings.xml"))])
class TexifySettings : PersistentStateComponent<TexifySettingsState> {

    companion object {
        @JvmStatic
        fun getInstance(): TexifySettings = ServiceManager.getService(TexifySettings::class.java)
    }

    // Options for smart quote replacement, in the order as they appear in the combobox
    enum class QuoteReplacement {
        NONE,
        LIGATURES,
        COMMANDS,
        CSQUOTES // Context Sensitive quotes from the csquotes package
    }

    var automaticSecondInlineMathSymbol = true
    var automaticUpDownBracket = true
    var automaticItemInItemize = true
    var automaticDependencyCheck = true
    var autoCompile = false
    var continuousPreview = false
    var includeBackslashInSelection = false
    var showPackagesInStructureView = false
    var automaticQuoteReplacement = QuoteReplacement.NONE
    var missingLabelMinimumLevel = LatexRegularCommand.SUBSECTION
    var pdfViewer = PdfViewer.values().first { it.isAvailable() }

    override fun getState(): TexifySettingsState? {
        return TexifySettingsState(
                automaticSecondInlineMathSymbol = automaticSecondInlineMathSymbol,
                automaticUpDownBracket = automaticUpDownBracket,
                automaticItemInItemize = automaticItemInItemize,
                automaticDependencyCheck = automaticDependencyCheck,
                autoCompile = autoCompile,
                continuousPreview = continuousPreview,
                includeBackslashInSelection = includeBackslashInSelection,
                showPackagesInStructureView = showPackagesInStructureView,
                automaticQuoteReplacement = automaticQuoteReplacement,
                missingLabelMinimumLevel = missingLabelMinimumLevel,
                pdfViewer = pdfViewer
        )
    }

    override fun loadState(state: TexifySettingsState) {
        automaticSecondInlineMathSymbol = state.automaticSecondInlineMathSymbol
        automaticUpDownBracket = state.automaticUpDownBracket
        automaticItemInItemize = state.automaticItemInItemize
        automaticDependencyCheck = state.automaticDependencyCheck
        autoCompile = state.autoCompile
        continuousPreview = state.continuousPreview
        includeBackslashInSelection = state.includeBackslashInSelection
        showPackagesInStructureView = state.showPackagesInStructureView
        automaticQuoteReplacement = state.automaticQuoteReplacement
        missingLabelMinimumLevel = state.missingLabelMinimumLevel
        pdfViewer = state.pdfViewer
    }
}
