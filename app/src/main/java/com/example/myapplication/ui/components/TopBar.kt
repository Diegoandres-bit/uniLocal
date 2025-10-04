import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun TopBar(
    imageId: Int,
    text: String,
    icon1: ImageVector,
    icon2: ImageVector,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = stringResource(R.string.ULogo),
                modifier = Modifier.size(42.dp) // tamaño del logo
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Row {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(colorResource(R.color.lightgreen))
                    .size(36.dp)

            ) {
                Icon(
                    imageVector = icon1,
                    contentDescription = "Icon 1",
                )
            }

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(colorResource(R.color.lightgreen))
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = icon2,
                    contentDescription = "Icon 2",
                )
            }
        }
    }
}
