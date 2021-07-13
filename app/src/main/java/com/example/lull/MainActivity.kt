package com.example.lull

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lull.ui.theme.LullTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import kotlin.collections.ArrayList

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LullTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Navigation()
                }
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun Navigation(){
    val navController = rememberNavController()
    val wallpaperList = remember {
        arrayListOf(
            Wallpaper(1,"Mountain and River", R.drawable.w_mountain_and_river, listOf("mountain","river","nature","trees"),"Mikael Gustafsson"),
            Wallpaper(2,"Bokeh Full Moon", R.drawable.w_bokeh_full_moon, listOf("bokeh", "moon","nature")),
            Wallpaper(3,"Mountain Morning", R.drawable.w_mountain_morning, listOf("mountain", "morning","nature"),"Stijn de Vries"),
            Wallpaper(4,"Pixel Sky", R.drawable.w_pixel_sky, listOf("pixel","mountain","sky","starts","night","nature")),
            Wallpaper(5,"Birds over the Trees", R.drawable.w_bird_over_the_trees, listOf("sky","birds","trees","nature","morning")),
            Wallpaper(6,"Deers", R.drawable.w_deers, listOf("animals","deer","nature","sky")),
            Wallpaper(7,"Train at Midnight", R.drawable.w_train_at_midnight, listOf("train","midnight","sky","stars")),
            Wallpaper(8,"Lonely Sunset", R.drawable.w_lonely_sunset, listOf("sunset","sky","girl","bridge")),
        )
    }
    NavHost(navController = navController
        , startDestination = "homepage") {
        composable("homepage"){
            Homepage(navController, wallpaperList)
        }
        composable("homepage/{tag}",
        arguments = listOf(
            navArgument("tag"){type = NavType.StringType}
        )){
            val tag = it.arguments?.getString("tag")!!
            Homepage(navController, wallpaperList, tag)
        }
        composable("details_screen/{wallpaper_id}"
            , arguments = listOf(
                navArgument("wallpaper_id"){ type = NavType.IntType}
            )){
            val itemId = it.arguments?.getInt("wallpaper_id")!!
            val wallpaper : Wallpaper? = searchList(wallpaperList,itemId)
            if(wallpaper != null){
                DetailsScreen(navController, wallpaper)
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun Homepage(navController: NavController, wallpaperList : ArrayList<Wallpaper>, tag : String? = null){
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val filteredList = getFilteredList(wallpaperList,textState)
    val padding = PaddingValues(20.dp,0.dp)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(0.dp, 20.dp)) {
        SearchView(padding, textState)
        Title(textState, filteredList.size)
        if(filteredList.isEmpty()){
            Box(modifier = Modifier.weight(1f))
        }else{
            Pager(list = filteredList){ wallpaper ->
                navController.navigate("details_screen/${wallpaper.id}")
            }
        }
        BottomText(padding)

    }
    SideEffect {
        if (tag!=null){
            textState.value = TextFieldValue(tag)
        }
    }

}


@Composable
fun BottomText(padding: PaddingValues){
    Column(modifier = Modifier.padding(paddingValues = padding)){
        Text(text = stringResource(id = R.string.app_name).uppercase()
            , style = MaterialTheme.typography.h1)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = stringResource(id = R.string.subtitle),
            style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun Title(state: MutableState<TextFieldValue>,
          filteredListSize : Int){
    val searchedText = state.value.text

    val title : String = if (searchedText.isEmpty()) {
        stringResource(id = R.string.all_wallpapers)
    }else{
        when(filteredListSize){
            0 -> stringResource(id = R.string.search_none)
            1 -> stringResource(id = R.string.search_one)
            else -> stringResource(id = R.string.search_many,filteredListSize)
        }

    }
    Text(modifier = Modifier.padding(20.dp,5.dp),
        text = title.uppercase(),
        style = MaterialTheme.typography.h2,
        maxLines = 1)
}

@ExperimentalMaterialApi
@Composable
fun ColumnScope.Pager(list : ArrayList<Wallpaper>, onItemSelected: (Wallpaper) -> Unit){
    LazyRow(modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(0.dp, 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        itemsIndexed(items = list){ position, wallpaper ->
            val padding : PaddingValues = when(position){
                0 -> PaddingValues(16.dp,0.dp,0.dp,0.dp)
                list.size - 1 -> PaddingValues(0.dp,0.dp,16.dp,0.dp)
                else -> PaddingValues(0.dp,0.dp,0.dp,0.dp)
            }
            val modifier = Modifier
                .padding(padding)
                .aspectRatio(0.5625f)
            WallpaperCard(wallpaper, modifier, onItemSelected)
        }
    }
}



@ExperimentalMaterialApi
@Composable
fun WallpaperCard(wallpaper : Wallpaper,
         modifier : Modifier,
         onItemSelected: (Wallpaper) -> Unit){
    Card( shape = RoundedCornerShape(15.dp),
        elevation = 5.dp,
        modifier = modifier,
        onClick = { onItemSelected.invoke(wallpaper)}
    ){
        Image(painter = painterResource(id = wallpaper.drawableId),
            contentDescription = wallpaper.name,
            contentScale = ContentScale.Crop )
    }
}

@Composable
fun SearchView(padding : PaddingValues, state: MutableState<TextFieldValue>) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(padding),
        horizontalArrangement = Arrangement.spacedBy(10.dp)){
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(32.dp))
        TextField(
            value = state.value,
            onValueChange = { value ->
                state.value = value
            },
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.body1,
            trailingIcon = {
                if (state.value == TextFieldValue("")) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search),
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }else{
                    IconButton(
                        onClick = {
                            state.value =
                                TextFieldValue("")
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close),
                            modifier = Modifier
                                .padding(15.dp)
                                .size(24.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.textFieldColors(

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

}

fun searchList(list : ArrayList<Wallpaper>, id : Int) : Wallpaper?{
    for(wallpaper in list){
        if(wallpaper.id == id){
            return wallpaper
        }
    }
    return null
}

fun getFilteredList(list : ArrayList<Wallpaper>, state : MutableState<TextFieldValue>) : ArrayList<Wallpaper> {
    val filteredWallpapers: ArrayList<Wallpaper>
    val searchedText = state.value.text

    if (searchedText.isEmpty()) {
        filteredWallpapers = list
    }else{
        val resultList = ArrayList<Wallpaper>()
        for(wallpaper in list){
            for(tag in wallpaper.tags){
                if(searchedText.lowercase().contains(tag)){
                    resultList.add(wallpaper)
                    break
                }
            }
        }
        filteredWallpapers = resultList
    }

    return filteredWallpapers
}
