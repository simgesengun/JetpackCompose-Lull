package com.example.lull

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.lull.ui.theme.Gray
import com.example.lull.ui.theme.SemiTransparent50
import com.example.lull.ui.theme.SemiTransparent20
import kotlinx.coroutines.*
import java.io.File

@ExperimentalMaterialApi
@Composable
fun DetailsScreen(navController: NavController, wallpaper : Wallpaper){
    val context = LocalContext.current
    val modifierPadding = Modifier.padding(20.dp,0.dp)

    val launcher : ManagedActivityResultLauncher<String, Boolean> = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val bitmap : Bitmap = BitmapFactory.decodeResource(context.resources,wallpaper.drawableId)
            FileUtils(context).saveImage(bitmap, wallpaper.name)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(0.dp, 20.dp),
        verticalArrangement = Arrangement.SpaceBetween){
        Box(modifier = Modifier
            .weight(1f)
            .aspectRatio(0.5625f)
            .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center) {
            WallpaperCard(wallpaper = wallpaper, modifier = Modifier.fillMaxSize()) {

            }
            Icon(painter = painterResource(id = R.drawable.ic_down),
                contentDescription = stringResource(id = R.string.download),
                modifier = Modifier.fillMaxSize(0.2f).clickable {
                    MainScope().launch {
                        saveImage(context, wallpaper, launcher)
                    }
                },
                tint = SemiTransparent50)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column{
            Text(text = wallpaper.name, style = MaterialTheme.typography.h1, modifier = modifierPadding)
            RowArtist(artist = wallpaper.artist, modifier = modifierPadding){
                Toast.makeText(context, context.getString(R.string.artist_unknown_info),Toast.LENGTH_SHORT).show()
            }
            Spacer(modifier = Modifier.height(10.dp))
            RowTags(tagList = wallpaper.tags){ tag ->
                navController.navigate("homepage/${tag}")
            }
        }
    }



}

@Composable
fun RowArtist(artist : String?, modifier : Modifier, infoOnClick : () -> Unit){
    when(artist){
        null -> {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically, modifier = modifier){
                Text(text = stringResource(id = R.string.artist_unknown),
                    style = MaterialTheme.typography.subtitle1)
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = stringResource(id = R.string.info),
                    tint = Gray,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = infoOnClick))
            }
        }
        else -> {
            Text(text = stringResource(id = R.string.artist,artist),
                style = MaterialTheme.typography.subtitle1,
                modifier = modifier)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun RowTags(tagList : List<String>, onItemSelected: (String) -> Unit){
    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        itemsIndexed(items = tagList){ position, tag ->
            val padding : PaddingValues = when(position){
                0 -> PaddingValues(16.dp,0.dp,0.dp,0.dp)
                tagList.size - 1 -> PaddingValues(0.dp,0.dp,16.dp,0.dp)
                else -> PaddingValues(0.dp,0.dp,0.dp,0.dp)
            }
            Card(shape = RoundedCornerShape(5.dp),
                backgroundColor = SemiTransparent20,
                elevation = 0.dp,
                modifier = Modifier.padding(padding),
                onClick = {
                    onItemSelected.invoke(tag)

                }){
                Text(text = tag, style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(10.dp,5.dp))
            }
        }
    }
}

private suspend fun saveImage(context : Context, wallpaper : Wallpaper, launcher : ManagedActivityResultLauncher<String, Boolean>) {
    val bitmap : Bitmap = BitmapFactory.decodeResource(context.resources,wallpaper.drawableId)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        withContext(Dispatchers.IO) {
            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val dirDest = File(Environment.DIRECTORY_PICTURES, context.getString(R.string.app_name))
            val date = System.currentTimeMillis()
            val extension = ".jpeg"
            val newImage = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${wallpaper.name}.$extension")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/$extension")
                put(MediaStore.MediaColumns.DATE_ADDED, date)
                put(MediaStore.MediaColumns.DATE_MODIFIED, date)
                put(MediaStore.MediaColumns.SIZE, bitmap.byteCount)
                put(MediaStore.MediaColumns.WIDTH, bitmap.width)
                put(MediaStore.MediaColumns.HEIGHT, bitmap.height)
                put(MediaStore.MediaColumns.RELATIVE_PATH, "$dirDest${File.separator}")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val newImageUri = context.contentResolver.insert(collection, newImage)
            context.contentResolver.openOutputStream(newImageUri!!, "w").use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            newImage.clear()
            newImage.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(newImageUri, newImage, null, null)
        }

    } else {
        when( ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )) {
            PackageManager.PERMISSION_GRANTED -> {
                FileUtils(context).saveImage(bitmap, wallpaper.name)

            } else -> {
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}