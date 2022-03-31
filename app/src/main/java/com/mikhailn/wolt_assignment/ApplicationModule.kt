package com.mikhailn.wolt_assignment

import com.mikhailn.wolt_assignment.data.domain.Place
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

class LoopedPlaces(private val places: List<Place>): Iterable<Place> {
    override fun iterator(): Iterator<Place> {
        return LoopedPlacesIterator(places)
    }
}

private class LoopedPlacesIterator(private val places: List<Place>): Iterator<Place> {
    var position = 0

    override fun hasNext(): Boolean {
        return true
    }

    override fun next(): Place {
        val next = position
        position++
        if (position == places.size - 1) {
            position = 0
        }

        return places[next]
    }
}

private val predefinedPlaces = listOf(
    Place("60.170187", "24.930599"),
    Place("60.169418", "24.931618"),
    Place("60.169818", "24.932906"),
    Place("60.170005", "24.935105"),
    Place("60.169108", "24.936210"),
    Place("60.168355", "24.934869"),
    Place("60.167560", "24.932562"),
    Place("60.168254", "24.931532"),
    Place("60.169012", "24.930341"),
    Place("60.170085", "24.929569")
)

@Module
@InstallIn(ViewModelComponent::class)
object ApplicationModule {

    @Provides
    fun provideLoopedPlaces(): LoopedPlaces {
        return LoopedPlaces(predefinedPlaces)
    }
}
