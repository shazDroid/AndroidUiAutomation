package com.shazdroid.automation.uiautomation.presentation.services.impl

import com.shazdroid.automation.uiautomation.data.model.UIComponent
import com.shazdroid.automation.uiautomation.data.source.ConfigService
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileInputStream

class BlocklyServiceImpl {

    fun generateBlocklyBlocks(
        layoutFilePath: String,
        configService: ConfigService
    ): Pair<String, List<String>> {
        val blockDefinitions = StringBuilder()
        val blockTypeNames = mutableListOf<String>()

        // Parse the layout file to get the list of UIComponents
        val components = parseLayoutFile(layoutFilePath)

        // Map component types to block colors
        val typeColourMap = mapOf(
            "Button" to 160,
            "TextView" to 230,
            "EditText" to 20
            // Add other component types and their corresponding colors
        )

        components.forEach { component ->
            val actions = configService.getActionsForComponentType(component.type)
            if (actions.isEmpty()) {
                // Skip components that have no actions defined
                return@forEach
            }

            actions.forEach { actionName ->
                val actionConfig = configService.getActionDetails(actionName) ?: return@forEach

                // Use the component's path for uniqueness
                val sanitizedPath = component.path.replace("/", "_").replace("-", "_")
                val blockTypeName = "${sanitizedPath}_${component.type}_${actionName}"
                blockTypeNames.add(blockTypeName)
                val blockColour = typeColourMap[component.type] ?: 230

                // Start defining the block
                blockDefinitions.append(
                    """
                Blockly.Blocks['$blockTypeName'] = {
                  init: function() {
                    this.appendDummyInput()
                        .appendField("${actionConfig.label} ${componentDisplayName(component)}");
                """.trimIndent()
                )

                // Dynamically add fields based on action configuration
                actionConfig.fields?.forEach { field ->
                    when (field.type) {
                        "input" -> {
                            blockDefinitions.append(
                                """
                            this.appendDummyInput()
                                .appendField("${field.placeholder}:")
                                .appendField(new Blockly.FieldTextInput(''), '${field.name}');
                            """.trimIndent()
                            )
                        }

                        "dropdown" -> {
                            val options = field.options?.joinToString(", ") { "['$it', '$it']" }
                            blockDefinitions.append(
                                """
                            this.appendDummyInput()
                                .appendField("${field.placeholder}:")
                                .appendField(new Blockly.FieldDropdown([${options}]), '${field.name}');
                            """.trimIndent()
                            )
                        }
                        // Add other field types as needed
                    }
                }

                // Set block properties to allow attachment and assign colors
                blockDefinitions.append(
                    """
                            this.setPreviousStatement(true, null);
                            this.setNextStatement(true, null);
                            this.setColour($blockColour);
                            this.setTooltip("${actionConfig.tooltip ?: actionConfig.label}");
                            this.setHelpUrl("");
                          }
                        };
                """.trimIndent()
                )

                // Generate the JavaScript code for the block (if needed)
                blockDefinitions.append(
                    """
                Blockly.JavaScript['$blockTypeName'] = function(block) {
                  var code = '';
                  // Generate code based on block inputs (if applicable)
                  return code;
                };
                """.trimIndent()
                )
            }
        }

        return Pair(blockDefinitions.toString(), blockTypeNames)
    }


    private fun componentDisplayName(component: UIComponent): String {
        // Create a user-friendly display name for the component
        // For example, replace slashes with arrows
        return component.path.replace("/", " > ")
    }

    private fun parseLayoutFile(layoutFilePath: String, parentPath: String = ""): List<UIComponent> {
        val components = mutableListOf<UIComponent>()
        val parserFactory = XmlPullParserFactory.newInstance()
        val parser = parserFactory.newPullParser()

        val layoutFile = File(layoutFilePath)
        if (!layoutFile.exists()) {
            // Handle missing layout file
            println("Layout file not found: $layoutFilePath")
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
                    val includedLayoutPath = resolveLayoutFilePath(layoutFile.parentFile, includedLayoutName)
                    val includedComponents = parseLayoutFile(includedLayoutPath, parentPath)
                    components.addAll(includedComponents)
                } else {
                    // Handle regular view or view group
                    val idAttribute = parser.getAttributeValue(null, "android:id")
                    val id = idAttribute?.substringAfter("@+id/")?.substringAfter("@id/")
                    val type = tagName
                    val currentPath = if (id != null && id.isNotEmpty()) {
                        if (parentPath.isNotEmpty()) "$parentPath/$id" else id
                    } else parentPath

                    if (isViewGroup(type)) {
                        // Do not add the ViewGroup to components, but parse its children
                        components.addAll(parseLayoutChildren(parser, currentPath))
                    } else {
                        // Add the component to the list if it's not a ViewGroup
                        val component = UIComponent(
                            id = id,
                            type = type,
                            parentId = null, // Adjust if you need parent references
                            path = currentPath
                        )
                        components.add(component)
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                // Handle end of tag if necessary
            }
            eventType = parser.next()
        }
        return components
    }


    private fun parseLayoutChildren(parser: XmlPullParser, parentPath: String): List<UIComponent> {
        val components = mutableListOf<UIComponent>()
        var eventType = parser.next()
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                val tagName = parser.name

                if (tagName == "include") {
                    // Handle include tag within children
                    val layoutAttribute = parser.getAttributeValue(null, "layout")
                    val includedLayoutName = layoutAttribute.substringAfter("@layout/")
                    val includedLayoutPath = resolveLayoutFilePath(File(parser.positionDescription), includedLayoutName)
                    val includedComponents = parseLayoutFile(includedLayoutPath, parentPath)
                    components.addAll(includedComponents)
                } else {
                    // Handle child views
                    val idAttribute = parser.getAttributeValue(null, "android:id")
                    val id = idAttribute?.substringAfter("@+id/")?.substringAfter("@id/")
                    val type = tagName
                    val currentPath = if (id != null && id.isNotEmpty()) {
                        "$parentPath/$id"
                    } else parentPath

                    if (isViewGroup(type)) {
                        // Do not add the ViewGroup to components, but parse its children
                        components.addAll(parseLayoutChildren(parser, currentPath))
                    } else {
                        // Add the component to the list if it's not a ViewGroup
                        val component = UIComponent(
                            id = id,
                            type = type,
                            parentId = null, // Adjust if you need parent references
                            path = currentPath
                        )
                        components.add(component)
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (isViewGroup(parser.name)) {
                    // End of the current ViewGroup
                    break
                }
            }
            eventType = parser.next()
        }
        return components
    }


    private fun isViewGroup(tagName: String): Boolean {
        // List of ViewGroup tags; adjust as needed
        val viewGroups = setOf(
            "LinearLayout",
            "RelativeLayout",
            "ConstraintLayout",
            "FrameLayout",
            "androidx.constraintlayout.widget.ConstraintLayout",
            "androidx.coordinatorlayout.widget.CoordinatorLayout",
            "androidx.recyclerview.widget.RecyclerView",
            // Add other ViewGroup types as needed
        )
        return tagName in viewGroups
    }

    private fun resolveLayoutFilePath(currentDir: File, layoutName: String): String {
        // Adjust this method to correctly resolve the layout file path
        // You might have a specific directory where layouts are stored
        val layoutsDir = currentDir.parentFile // Assuming layouts are in a parent directory
        return "${layoutsDir.absolutePath}/$layoutName.xml"
    }
}
