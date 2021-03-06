package nl.hannahsten.texifyidea.inspections.latex

import nl.hannahsten.texifyidea.file.LatexFileType
import nl.hannahsten.texifyidea.inspections.TexifyInspectionTestBase

class LatexLabelConventionInspectionTest : TexifyInspectionTestBase(LatexLabelConventionInspection()) {
    fun testSectionLabelConventionWarning() {
        myFixture.configureByText(LatexFileType, """
            \begin{document}
                \section{some section}
                <weak_warning descr="Unconventional label prefix">\label{some-section}</weak_warning>
            \end{document}
        """.trimIndent())
        myFixture.checkHighlighting(false, false, true, false)
    }

    fun testFigureLabelConventionWarning() {
        myFixture.configureByText(LatexFileType, """
            \begin{document}
                \begin{figure}
                    <weak_warning descr="Unconventional label prefix">\label{some-figure}</weak_warning>
                \end{figure}
            \end{document}
        """.trimIndent())
        myFixture.checkHighlighting(false, false, true, false)
    }

    fun testListingLabelConventionWarning() {
        myFixture.configureByText(LatexFileType, """
            \begin{document}
                <weak_warning descr="Unconventional label prefix">\begin{lstlisting}[label={some label}]
                \end{lstlisting}</weak_warning>
            \end{document}
        """.trimIndent())
        myFixture.checkHighlighting(false, false, true, false)
    }

    fun testListingLabelConventionQuickFix() {
        testQuickFix("""
            \begin{document}
                \begin{lstlisting}[label=somelabel]
                \end{lstlisting}
                \ref{somelabel}
                \cref{somelabel}
            \end{document}
        """.trimIndent(), """
            \begin{document}
                \begin{lstlisting}[label={lst:somelabel}]
                \end{lstlisting}
                \ref{lst:somelabel}
                \cref{lst:somelabel}
            \end{document}
        """.trimIndent())
    }

    fun testListingLabelConventionQuickFixWithGroup() {
        testQuickFix("""
            \begin{document}
                \begin{lstlisting}[label={some label}]
                \end{lstlisting}
                \ref{some label}
                \cref{some label}
            \end{document}
        """.trimIndent(), """
            \begin{document}
                \begin{lstlisting}[label={lst:some-label}]
                \end{lstlisting}
                \ref{lst:some-label}
                \cref{lst:some-label}
            \end{document}
        """.trimIndent())
    }

    fun testFigureLabelConventionQuickFix() {
        testQuickFix("""
            \begin{document}
                \begin{figure}
                    \label{some label}
                \end{figure}
                \ref{some label}
                \cref{some label}
            \end{document}
        """.trimIndent(), """
            \begin{document}
                \begin{figure}
                    \label{fig:some-label}
                \end{figure}
                \ref{fig:some-label}
                \cref{fig:some-label}
            \end{document}
        """.trimIndent())
    }

    fun testSectionLabelConventionQuickFix() {
        testQuickFix("""
            \begin{document}
                \section{some section}
                \label{some label}
                \ref{some label}
                \cref{some label}
            \end{document}
        """.trimIndent(), """
            \begin{document}
                \section{some section}
                \label{sec:some-label}
                \ref{sec:some-label}
                \cref{sec:some-label}
            \end{document}
        """.trimIndent())
    }
}