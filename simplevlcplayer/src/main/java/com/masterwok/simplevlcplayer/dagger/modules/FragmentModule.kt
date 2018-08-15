package com.masterwok.simplevlcplayer.dagger.modules

import com.masterwok.simplevlcplayer.fragments.LocalPlayerFragment
import com.masterwok.simplevlcplayer.fragments.RendererPlayerFragment
import com.masterwok.simplevlcplayer.fragments.SubtitlesDialogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
@Suppress("unused")
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeLocalPlayerFragment(): LocalPlayerFragment

    @ContributesAndroidInjector
    abstract fun contributeRendererPlayerFragment(): RendererPlayerFragment

    @ContributesAndroidInjector
    abstract fun contributesSubtitlesDialogFragment(): SubtitlesDialogFragment

}
