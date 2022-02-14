package cz.scilifapp.ui.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is a free space\nwaiting to be filled with joy"
    }
    val text: LiveData<String> = _text
}