package com.shazdroid.automation.uiautomation.data.source

import com.shazdroid.automation.uiautomation.data.model.UIComponent
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileInputStream

class LayoutParser(private val layoutsDirectory: String) {

    fun parseLayout(
        layoutName: String,
        parentComponent: UIComponent? = null,
        parentPath: String = ""
    ): List<UIComponent> {
        val components = mutableListOf<UIComponent>()
        val parserFactory = XmlPullParserFactory.newInstance()
        val parser = parserFactory.newPullParser()

        val layoutFile = getLayoutFile(layoutName)
        if (layoutFile == null || !layoutFile.exists()) {
            // Handle missing layout file
            return components
        }

        parser.setInput(FileInputStream(layoutFile), null)

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                val tagName = parser.name

                if (tagName == "include") {
                    // Handle include tag
                    val layoutAttribute = parser.getAttributeValue(null, "layout")
                    val includedLayoutName = layoutAttribute.substringAfter("@layout/")
                    val includedComponents = parseLayout(includedLayoutName, parentComponent, parentPath)
                    components.addAll(includedComponents)
                } else {
                    // Handle regular view or view group
                    val idAttribute = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "id")
                    val id = idAttribute?.substringAfter("@+id/")?.substringAfter("@id/")
                    val type = tagName
                    val currentPath = if (id != null && id.isNotEmpty()) {
                        if (parentPath.isNotEmpty()) "$parentPath/$id" else id
                    } else parentPath

                    val component = UIComponent(
                        id = id,
                        type = type,
                        parentId = parentComponent?.id,
                        path = currentPath
                    )

                    // If the tag is a ViewGroup, parse its children
                    if (isViewGroup(type)) {
                        component.children.addAll(parseLayoutChildren(parser, component, currentPath))
                    }

                    components.add(component)
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.name == parentComponent?.type) {
                    // End of this view group
                    break
                }
            }
            eventType = parser.next()
        }
        return components
    }

    private fun parseLayoutChildren(
        parser: XmlPullParser,
        parentComponent: UIComponent,
        parentPath: String
    ): List<UIComponent> {
        val components = mutableListOf<UIComponent>()
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                val tagName = parser.name

                if (tagName == "include") {
                    // Handle include tag within children
                    val layoutAttribute = parser.getAttributeValue(null, "layout")
                    val includedLayoutName = layoutAttribute.substringAfter("@layout/")
                    val includedComponents = parseLayout(includedLayoutName, parentComponent, parentPath)
                    components.addAll(includedComponents)
                } else {
                    // Handle child views
                    val idAttribute = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "id")
                    val id = idAttribute?.substringAfter("@+id/")?.substringAfter("@id/")
                    val type = tagName
                    val currentPath = if (id != null && id.isNotEmpty()) {
                        "$parentPath/$id"
                    } else parentPath

                    val component = UIComponent(
                        id = id,
                        type = type,
                        parentId = parentComponent.id,
                        path = currentPath
                    )

                    // If the tag is a ViewGroup, parse its children recursively
                    if (isViewGroup(type)) {
                        component.children.addAll(parseLayoutChildren(parser, component, currentPath))
                    }

                    components.add(component)
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.name == parentComponent.type) {
                    // End of this view group
                    break
                }
            }
            eventType = parser.next()
        }
        return components
    }

    private fun isViewGroup(tagName: String): Boolean {
        // Return true if the tagName represents a ViewGroup
        val viewGroups = setOf(
            "LinearLayout",
            "RelativeLayout",
            "ConstraintLayout",
            "FrameLayout",
            "androidx.constraintlayout.widget.ConstraintLayout",
            "androidx.coordinatorlayout.widget.CoordinatorLayout",
            "androidx.recyclerview.widget.RecyclerView",
            "androidx.cardview.widget.CardView",
            "com.google.android.material.bottomsheet.BottomSheetDialog",
            "com.google.android.material.bottomsheet.BottomSheetDialogFragment",
            // Add other ViewGroup types as needed
        )
        return tagName in viewGroups
    }

    private fun getLayoutFile(layoutName: String): File? {
        // Construct the file path based on the layout name
        val filePath = "$layoutsDirectory/$layoutName.xml"
        val file = File(filePath)
        return if (file.exists()) file else null
    }
}
