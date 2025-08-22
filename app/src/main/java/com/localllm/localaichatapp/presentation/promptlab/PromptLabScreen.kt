package com.localllm.localaichatapp.presentation.promptlab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localllm.localaichatapp.domain.model.PromptTemplate
import com.localllm.localaichatapp.presentation.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptLabScreen(
    modelId: String,
    onBackClick: () -> Unit,
    onNavigateToPerformance: () -> Unit,
    viewModel: PromptLabViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(modelId) {
        viewModel.initializeSession(modelId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PromptLabTopBar(
            modelName = uiState.modelName,
            onBackClick = onBackClick,
            onPerformanceClick = onNavigateToPerformance
        )

        if (uiState.isLoading && uiState.templates.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Template Categories
                item {
                    TemplateCategoriesSection(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = viewModel::selectCategory
                    )
                }

                // Template Selection
                item {
                    TemplateSelectionSection(
                        templates = uiState.filteredTemplates,
                        selectedTemplate = uiState.selectedTemplate,
                        onTemplateSelected = viewModel::selectTemplate
                    )
                }

                // Parameter Input Section
                uiState.selectedTemplate?.let { template ->
                    item {
                        ParameterInputSection(
                            template = template,
                            parameterValues = uiState.parameterValues,
                            onParameterChanged = viewModel::updateParameter
                        )
                    }
                }

                // Preview Section
                if (uiState.processedPrompt.isNotBlank()) {
                    item {
                        PromptPreviewSection(
                            processedPrompt = uiState.processedPrompt,
                            onClearPrompt = viewModel::clearPrompt
                        )
                    }
                }

                // Response Section
                if (uiState.response.isNotBlank() || uiState.isGenerating) {
                    item {
                        ResponseSection(
                            response = uiState.response,
                            isGenerating = uiState.isGenerating,
                            metadata = uiState.responseMetadata
                        )
                    }
                }
            }

            // Generate Button
            GenerateButton(
                onGenerateClick = viewModel::generateResponse,
                isEnabled = uiState.processedPrompt.isNotBlank() && !uiState.isGenerating,
                isGenerating = uiState.isGenerating
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromptLabTopBar(
    modelName: String,
    onBackClick: () -> Unit,
    onPerformanceClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Prompt Lab",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = modelName.ifBlank { "Loading..." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onPerformanceClick) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Performance Insights",
                    tint = MaterialTheme.extendedColors.infoColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun TemplateCategoriesSection(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Column {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    text = "All",
                    isSelected = selectedCategory == null,
                    onClick = { onCategorySelected(null) }
                )
            }
            
            items(categories) { category ->
                CategoryChip(
                    text = category,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(text) },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.extendedColors.promptLabColor,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
private fun TemplateSelectionSection(
    templates: List<PromptTemplate>,
    selectedTemplate: PromptTemplate?,
    onTemplateSelected: (PromptTemplate) -> Unit
) {
    Column {
        Text(
            text = "Select Template",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (templates.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No templates found in this category",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            templates.forEach { template ->
                TemplateCard(
                    template = template,
                    isSelected = selectedTemplate?.id == template.id,
                    onSelected = { onTemplateSelected(template) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: PromptTemplate,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.extendedColors.promptLabColor)
        } else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.extendedColors.promptLabColor.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (template.isBuiltIn) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Built-in",
                            tint = MaterialTheme.extendedColors.promptLabColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = template.category.name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            if (template.parameters.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Parameters: ${template.parameters.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ParameterInputSection(
    template: PromptTemplate,
    parameterValues: Map<String, String>,
    onParameterChanged: (String, String) -> Unit
) {
    Column {
        Text(
            text = "Template Parameters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        template.parameters.forEach { parameter ->
            OutlinedTextField(
                value = parameterValues[parameter] ?: "",
                onValueChange = { onParameterChanged(parameter, it) },
                label = { Text(parameter.replace("_", " ").replaceFirstChar { it.uppercaseChar() }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun PromptPreviewSection(
    processedPrompt: String,
    onClearPrompt: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Prompt Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            IconButton(onClick = onClearPrompt) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = processedPrompt,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun ResponseSection(
    response: String,
    isGenerating: Boolean,
    metadata: com.localllm.localaichatapp.domain.model.ResponseMetadata?
) {
    Column {
        Text(
            text = "AI Response",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (response.isNotBlank()) {
                    Text(
                        text = response,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                AnimatedVisibility(
                    visible = isGenerating,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier.padding(top = if (response.isNotBlank()) 8.dp else 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generating response...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Performance metadata
                metadata?.let { meta ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "⚡ ${meta.formattedTtft}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "🔄 ${meta.formattedDecodeSpeed}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "📊 ${meta.tokensGenerated} tokens",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerateButton(
    onGenerateClick: () -> Unit,
    isEnabled: Boolean,
    isGenerating: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FloatingActionButton(
                onClick = onGenerateClick,
                modifier = Modifier.size(56.dp),
                containerColor = if (isEnabled) {
                    MaterialTheme.extendedColors.promptLabColor
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Generate",
                        tint = if (isEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}