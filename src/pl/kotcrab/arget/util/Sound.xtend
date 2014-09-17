package pl.kotcrab.arget.util

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import pl.kotcrab.arget.App

class Sound {
	val Clip clip

	new(String path) {
		clip = AudioSystem.getClip()
		var ais = AudioSystem.getAudioInputStream(App.getResourceAsStream(path))
		clip.open(ais)
	}

	def play() {
		if (clip.isRunning == false) {
			clip.framePosition = 0
			clip.start()
		}
	}
}
