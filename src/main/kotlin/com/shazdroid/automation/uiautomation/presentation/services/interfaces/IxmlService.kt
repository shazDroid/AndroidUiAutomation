package com.shazdroid.automation.uiautomation.presentation.services.interfaces

import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.shazdroid.automation.uiautomation.data.model.UIComponent
import java.io.File

interface IxmlService {
    fun scanXmlFile(file: PsiFile): List<UIComponent>
}