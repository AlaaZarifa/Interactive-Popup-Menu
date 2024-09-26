package com.alaazarifa.interactivepopupmenu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.alaazarifa.interactivepopupmenu.ui.theme.InteractivePopupMenuTheme
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InteractivePopupMenuTheme {
                Surface() {
                    ContactsList()
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsList() {
    var showPopup by remember { mutableStateOf(false) }
    var applyOffest by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    var contacts by remember { mutableStateOf(initContacts()) }

    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {


        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentPadding = PaddingValues(
                vertical = 40.dp,
            ),
        ) {

            itemsIndexed(contacts) { index, contact ->
                val shadow = when {
                    showPopup && selectedIndex != index -> 0.dp
                    showPopup && selectedIndex == index -> 10.dp
                    else -> 2.dp
                }

                Box {
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = shadow,
                            pressedElevation = 0.dp,
                            disabledElevation = 0.dp
                        ),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                        onClick = {
                            selectedIndex = index
                            coroutineScope.launch {
                                handleItemClick(lazyListState, selectedIndex) { showOffest ->
                                    applyOffest = showOffest
                                }
                                showPopup = true
                            }
                        }) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Avatar(contact.name)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        contact.name,
                                        fontSize = 16.sp,
                                        color = Color.Black.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .offset(x = -5.dp)
                                            .wrapContentWidth(),
                                    )

                                    if (contact.isStarred) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_star_on),
                                            contentDescription = null,
                                            tint = Color(0xFFF7BB0A),
                                            modifier = Modifier
                                                .size(18.dp)
                                                .offset(y = -1.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.offset(x = 10.dp)
                                ) {

                                    SmallButton(R.drawable.ic_call, Color(0xFF38AF48)) {}
                                    Spacer(modifier = Modifier.width(3.dp))
                                    SmallButton(R.drawable.ic_chat, Color(0XFF26A6DA)) {}
                                    Spacer(modifier = Modifier.width(3.dp))
                                }

                            }
                        }
                    }

                    if (showPopup && selectedIndex == index) {
                        Popup(
                            offset = IntOffset(x = 0, y = if (applyOffest) -780 else 130),
                            onDismissRequest = {
                                showPopup = false
                                selectedIndex = -1
                            },
                            properties = PopupProperties(focusable = true, clippingEnabled = false),
                        ) {

                            MyPopupMenu(contact, onEdit = { newName ->

                                contacts = contacts.toMutableList().apply {
                                    this[index] = this[index].copy(name = newName)
                                }

                            }, onStarred = { starred ->

                                contacts = contacts.toMutableList().apply {
                                    this[index] = this[index].copy(isStarred = starred)
                                }

                            }, onShare = {

                                val shareText =
                                    "Contact Name: ${contact.name}\nContact Number: ${contact.numbers[0]}\nContact Number: ${contact.numbers[1]}"


                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)

                                showPopup = false

                                context.startActivity(shareIntent)


                            }, onDelete = {
                                contacts = contacts.toMutableList().apply {
                                    removeAt(index)
                                }
                                showPopup = false

                            })
                        }
                    }

                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }


        Crossfade(targetState = showPopup, label = "") { showPopup ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (showPopup) Color.Black.copy(alpha = 0.1f) else Color.Transparent)
            )
        }


    }


}

@Composable
private fun Avatar(contactName: String) {


    val initials = contactName.split(" ").joinToString("") { it.first().uppercase() }

    val bgColor = remember {
        Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat(),
            alpha = 0.2f
        )
    }
    Box(
        modifier = Modifier
            .offset(x = -15.dp)
            .size(50.dp)
            .clip(CircleShape)
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {

        Text(initials, fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SmallButton(@DrawableRes icon: Int, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(35.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 25.dp),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
    }


}

private fun handleItemClick(
    lazyListState: LazyListState,
    clickedIndex: Int,
    onClick: (Boolean) -> Unit
) {
    val layoutInfo = lazyListState.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    val totalVisibleItems = visibleItemsInfo.size
    val clickedVisibleIndex = clickedIndex - visibleItemsInfo.first().index
    if (clickedVisibleIndex > totalVisibleItems / 2) {
        val targetIndex = clickedIndex - totalVisibleItems / 4
        onClick(true)
    } else onClick(false)

}


fun initContacts(): List<Contact> {
    val names = listOf(
        "John Smith",
        "Mary Johnson",
        "Michael Brown",
        "Jennifer Davis",
        "James Wilson",
        "Elizabeth Taylor",
        "David Anderson",
        "Sarah Thomas",
        "Robert Miller",
        "Emily White",
        "William Harris",
        "Linda Martin",
        "Charles King",
        "Patricia Scott",
        "Christopher Moore"
    )

    return names.map { name ->
        Contact(name = name)
    }
}

data class Contact(
    val name: String,
    val isStarred: Boolean = false,
    val numbers: Array<String> = arrayOf("+358 40 0323 859", "+39 183 4648217")
)