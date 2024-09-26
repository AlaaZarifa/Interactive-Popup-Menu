package com.alaazarifa.interactivepopupmenu


import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ripple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyPopupMenu(
    contact: Contact,
    onEdit: (String) -> Unit,
    onStarred: (Boolean) -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var conName by remember { mutableStateOf(contact.name) }
    val context = LocalContext.current


    var isExpanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.9f,
        animationSpec = tween(
            durationMillis = 100, easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(Unit) {
        isExpanded = true
    }

    var isStared by remember { mutableStateOf(contact.isStarred) }
    var isEditable by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = conName)) }
    var areNumbersShown by remember { mutableStateOf(false) }
    var areShareOptionsShown by remember { mutableStateOf(false) }
    var enableDel by remember { mutableStateOf(false) }
//    var isChecked by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .shadow(
                elevation = 30.dp,
                shape = RoundedCornerShape(17.dp),
                spotColor = Color(0x27000000),
                ambientColor = Color(0xFFFFFFFF)

            )
            .padding(top = 5.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {

        Column {

            // Star
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topEnd = 17.dp,
                            topStart = 17.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
                    .clickable(
                        indication = ripple(color = Color(0x03000000)),
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isStared = !isStared
                        onStarred(isStared)
                    }

            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 20.dp),
                        text = if (isStared) "Unstar" else "Star",
//                        fontFamily = FontFamily.Monospace,
                        fontSize = 17.sp,
//                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = Color(0xFF414141)
                    )

                    Icon(
                        painterResource(id = if (isStared) R.drawable.ic_star_on else R.drawable.ic_star_off),
                        contentDescription = "",
                        tint = Color(0xFFF7BB0A),
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .size(26.dp)


                    )

                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )
            }

            //Edit
            Column {

                LaunchedEffect(isEditable) {
                    if (isEditable) {
                        focusRequester.requestFocus()
                        textFieldValue = TextFieldValue(
                            text = textFieldValue.text,
                            selection = TextRange(textFieldValue.text.length)
                        )
                        keyboardController?.show()
                    } else {
                        keyboardController?.hide()
                    }
                }

                DisposableEffect(Unit) {
                    onDispose {
                        keyboardController?.hide()
                    }
                }

                AnimatedContent(
                    targetState = isEditable,
                ) { targetState ->

                    if (targetState) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            BasicTextField(
                                value = textFieldValue,
                                onValueChange = { textFieldValue = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 15.dp, end = 10.dp)
                                    .background(
                                        Color(0xFFF1F1F1),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                textStyle = TextStyle(
                                    color = Color(0xFF505050),
                                    fontSize = 16.sp,
//                                    fontFamily = FontFamily.Monospace
                                ),
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                                decorationBox = { innerTextField ->
                                    if (textFieldValue.text.isEmpty()) {
                                        Text(
                                            "Edit Name",
                                            style = TextStyle(
                                                color = Color(0xFF9B9B9B),
                                                fontSize = 16.sp
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            Row(
                                modifier = Modifier
                                    .padding(end = 10.dp)
                            ) {
                                ElevatedButton(
                                    elevation = ButtonDefaults.elevatedButtonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 1.dp,
                                        disabledElevation = 4.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                        disabledContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    onClick = {
                                        conName = textFieldValue.text
                                        isEditable = false
                                        onEdit(conName)
                                    },
                                    interactionSource = remember { MutableInteractionSource() },
                                    modifier = Modifier
                                        .size(43.dp)
                                        .padding(5.dp),
                                    contentPadding = PaddingValues(0.dp) // Optional: Remove padding to make icon fit better

                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_check),
                                        contentDescription = "",
                                        tint = Color(0xF44BC20B),
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .size(23.dp)
                                    )
                                }

                                ElevatedButton(
                                    elevation = ButtonDefaults.elevatedButtonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 1.dp,
                                        disabledElevation = 4.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                        disabledContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    onClick = {
                                        isEditable = false
                                    },
                                    interactionSource = remember { MutableInteractionSource() },
                                    modifier = Modifier
                                        .size(43.dp)
                                        .padding(5.dp),
                                    contentPadding = PaddingValues(0.dp)

                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_x),
                                        contentDescription = "",
                                        tint = Color(0xF4E61414),
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .size(22.dp)
                                    )
                                }
                            }

                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = ripple(color = Color(0x03000000)),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    isEditable = true
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 20.dp, vertical = 17.dp),
                                text = "Edit Name",
                                fontSize = 17.sp,
                                textAlign = TextAlign.Start,
                                color = Color(0xFF414141)
                            )


                            Icon(
                                painterResource(id = R.drawable.ic_edit),
                                contentDescription = "",
                                tint = Color(0xFF414141),
                                modifier = Modifier
                                    .padding(end = 20.dp)
                                    .size(20.dp)

                            )
                        }
                    }


                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )


            }

            // Numbers
            Column {

                AnimatedContent(
                    targetState = areNumbersShown,
                ) { targetState ->

                    if (targetState) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 20.dp, end = 10.dp)
                            ) {

                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    Text(
                                        text = contact.numbers.first(),
                                        color = Color(0xFF4376CE),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                    )

                                    IconButton(onClick = {
                                        Toast.makeText(context, "Number Copied", Toast.LENGTH_SHORT)
                                            .show()
                                    }) {
                                        Icon(
                                            painterResource(id = R.drawable.ic_copy2),
                                            contentDescription = "",
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    Text(
                                        text = contact.numbers.last(),
                                        color = Color(0xFF4376CE),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                    )

                                    IconButton(onClick = {
                                        Toast.makeText(context, "Number Copied", Toast.LENGTH_SHORT)
                                            .show()

                                    }) {
                                        Icon(
                                            painterResource(id = R.drawable.ic_copy2),
                                            contentDescription = "",
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                }

                            }
                            Row(
                                modifier = Modifier
                                    .padding(end = 10.dp)
                            ) {

                                ElevatedButton(
                                    elevation = ButtonDefaults.elevatedButtonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 1.dp,
                                        disabledElevation = 4.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                        disabledContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    onClick = {
                                        areNumbersShown = false
                                    },
                                    interactionSource = remember { MutableInteractionSource() },
                                    modifier = Modifier
                                        .size(43.dp)
                                        .padding(5.dp),
                                    contentPadding = PaddingValues(0.dp)

                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_x),
                                        contentDescription = "",
                                        tint = Color(0xF4E61414),
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .size(22.dp)
                                    )
                                }
                            }

                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = ripple(color = Color(0x03000000)),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    areNumbersShown = true
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 20.dp, vertical = 17.dp),
                                text = "Show Phone Numbers",
                                fontSize = 17.sp,
                                textAlign = TextAlign.Start,
                                color = Color(0xFF414141)
                            )


                            Icon(
                                painterResource(id = R.drawable.ic_con),
                                contentDescription = "",
                                tint = Color(0xFF414141),
                                modifier = Modifier
                                    .padding(end = 20.dp)
                                    .size(22.dp)

                            )
                        }
                    }


                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )


            }

            // Share
            Column {

                AnimatedContent(
                    targetState = areShareOptionsShown,
                ) { targetState ->

                    if (targetState)
                    {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(
                                        start = 20.dp,
                                        end = 10.dp,
                                        top = 10.dp,
                                        bottom = 10.dp
                                    )
                            ) {


                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SmallCheckBox(false)

                                    Text(
                                        modifier = Modifier.padding(start = 10.dp),
                                        text = conName,
                                        color = Color(0xFF3F4B88),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium,
                                    )

                                }

                                Spacer(modifier = Modifier.height(7.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SmallCheckBox()

                                    Text(
                                        modifier = Modifier.padding(start = 10.dp),
                                        text = contact.numbers.first(),
                                        color = Color(0xFF3F4B88),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium,
                                    )

                                }
                                
                                Spacer(modifier = Modifier.height(7.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SmallCheckBox()

                                    Text(
                                        modifier = Modifier.padding(start = 10.dp),
                                        text = contact.numbers.last(),
                                        color = Color(0xFF3F4B88),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium,
                                    )

                                }


                            }

                            Column {

                                Row(
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                ) {

                                    ElevatedButton(
                                        elevation = ButtonDefaults.elevatedButtonElevation(
                                            defaultElevation = 7.dp,
                                            pressedElevation = 1.dp,
                                            disabledElevation = 4.dp
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                            disabledContainerColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        onClick = {
                                            areShareOptionsShown = false
                                            onShare()

                                        },
                                        interactionSource = remember { MutableInteractionSource() },
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(5.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(
                                            painterResource(id = R.drawable.ic_share2),
                                            contentDescription = "",
                                            tint = Color(0xFF000000),
                                            modifier = Modifier
                                                .size(27.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                ) {

                                    ElevatedButton(
                                        elevation = ButtonDefaults.elevatedButtonElevation(
                                            defaultElevation = 7.dp,
                                            pressedElevation = 1.dp,
                                            disabledElevation = 4.dp
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                            disabledContainerColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        onClick = {
                                            areShareOptionsShown = false
                                        },
                                        interactionSource = remember { MutableInteractionSource() },
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(5.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(
                                            painterResource(id = R.drawable.ic_x),
                                            contentDescription = "",
                                            tint = Color(0xF4E61414),
                                            modifier = Modifier
                                                .size(27.dp)
                                        )
                                    }
                                }
                            }

                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = ripple(color = Color(0x03000000)),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    areShareOptionsShown = true
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 20.dp, vertical = 17.dp),
                                text = "Share",
                                fontSize = 17.sp,
                                textAlign = TextAlign.Start,
                                color = Color(0xFF414141)
                            )


                            Icon(
                                painterResource(id = R.drawable.ic_share),
                                contentDescription = "",
                                tint = Color(0xFF414141),
                                modifier = Modifier
                                    .padding(end = 20.dp)
                                    .size(22.dp)
                            )
                        }
                    }


                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )


            }


            // Delete
            Column {

                AnimatedContent(
                    targetState = enableDel,
                ) { targetState ->

                    if (targetState) {

                        InteractiveDeleteRow(enableDel, onDeleteConfirmed = {
                            enableDel = false
                            Toast.makeText(context, "Contact Deleted", Toast.LENGTH_SHORT).show()
                            onDelete()
                        }) {

                            Box{

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 12.dp),
                                    text = "Hold to Confirm ",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFB91E1E)
                                )
                            }


                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clickable(
                                    indication = ripple(color = Color(0x03000000)),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    enableDel = true
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 20.dp, vertical = 17.dp),
                                text = "Delete Contact",
                                fontSize = 17.sp,
                                textAlign = TextAlign.Start,
                                color = Color(0xFFB91E1E)
                            )


                            Icon(
                                painterResource(id = R.drawable.ic_remove),
                                contentDescription = "",
                                tint = Color(0xFFB91E1E),
                                modifier = Modifier
                                    .padding(end = 20.dp)
                                    .size(22.dp)

                            )
                        }
                    }


                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )


            }


        }
    }


}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SmallCheckBox(allowSelection: Boolean = true) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false
    ) {
        var isChecked by remember { mutableStateOf(true) }

        Checkbox(
            enabled = if(allowSelection) true else false ,
            checked = if(allowSelection) isChecked else true,
            onCheckedChange = {
                isChecked = it
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF3F4B88), // Custom checked color
                uncheckedColor = Color(0xFF3F4B88), // Custom unchecked color (optional)
            )
        )
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun InteractiveDeleteRow(
    enableDel: Boolean,
    onDeleteConfirmed: () -> Unit,
    content: @Composable() (RowScope.() -> Unit)
) {

    var isPressed by remember { mutableStateOf(false) }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                )
            )
            if (animatedProgress.value >= 1f) {
                onDeleteConfirmed()
            }
        } else {
            animatedProgress.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color(0x0DB91E1E))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }

    ) {
        // Progress background
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress.value)
                .height(55.dp)
                .background(Color.Red.copy(alpha = 0.25f))
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}


fun Int.toDp(): Dp = this.dp
