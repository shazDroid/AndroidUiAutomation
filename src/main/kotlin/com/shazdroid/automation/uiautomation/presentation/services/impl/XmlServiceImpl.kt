package com.shazdroid.automation.uiautomation.presentation.services.impl

import com.intellij.openapi.Disposable
import com.intellij.psi.PsiFile
import com.intellij.psi.XmlRecursiveElementVisitor
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.shazdroid.automation.uiautomation.data.model.UIComponent
import com.shazdroid.automation.uiautomation.presentation.services.interfaces.IxmlService

class XmlServiceImpl : IxmlService, Disposable {
    //    override fun scanXmlFile(file: PsiFile): List<UIComponent> {
//        if (file !is XmlFile) return emptyList()
//
//        val components = mutableListOf<UIComponent>()
//        file.rootTag?.accept(object : XmlRecursiveElementVisitor() {
//            override fun visitXmlTag(tag: XmlTag) {
//                super.visitXmlTag(tag)
//                // Example of gathering component name and id
//                val componentName = tag.name
//                val componentId = tag.getAttributeValue("android:id")?.removePrefix("@+id/")
//
//                if (componentId != null) {
//                    components.add(UIComponent(type = componentName, id =  componentId))
//                }
//            }
//        })
//
//        return components
//    }
//
//    override fun dispose() {
//
//    }
    override fun scanXmlFile(file: PsiFile): List<UIComponent> {
        TODO()
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }
}