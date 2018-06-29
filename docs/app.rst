App
===

Messung
-------

Als Grundlage wurde der Stackoverflow Beitrag von `Daniel Hernandez Ramirez` :cite:`noauthor_audio_nodate` und
`Soham` :cite:`noauthor_audio_nodate-2` verwendet.

Der Algorhytmus ist:
``Dezibel = 20 * log10( Amplitude / ReferenceAmplitude)``

.. index:: Quellen

GPS
---

Es wid nur die letzte bekannte Position des Gerätes bestimmt, dazu wurde als Code Grundlage `swiftBoy`
:cite:`swiftboy_geolocation_nodate` verwendet.

.. index:: Quellen

Login
-----

Der Login wurde mittels der Android Studio vorgefertigten Activity ``Sign-in`` erstellt und mittels API request
für Opensensemap angepasst.

Gravatar
--------

Wenn der User erfolgreich eingeloggt ist, wird immer ein Bild von https://en.gravatar.com/ heruntergeladen. Wenn der User
bei Gravatar existiert wird das entsprechende Bild heruntergeladen, wenn der User sich nicht zuvor mit der gleichen
Email Addresse wie in Opensensemap registiert hat, wird ein default Gravatar Bild heruntergeladen.

Für die integration wurde auf den Beitrag von `Android Developer` :cite:`noauthor_bitmap_nodate` aufgebaut.

.. index:: Quellen

SQLite
------

Die SQL Integration wurde mit hilfe des Offiziellen Video Tutorial von Google :cite:`noauthor_contribute_2018` erstellt.

.. index:: Quellen

API
---

Für die API request wurde ``Android Asynchronous Http Client`` :cite:`smith_android_nodate` verwendet, welches
Rest API Zugriffe in Java handelt.

Um die Kommunikation zu OpenSenseMap zu verwirklichen, wurden alle Request basierend auf der ``OpenSenseMap API documentation``
:cite:`noauthor_opensensemap_nodate-1` erstellt.

.. index:: Quellen

Lautstärken Vergleich
---------------------

Text Quellen
^^^^^^^^^^^^

Die Dezibel Vergleichswerte wurde vonn http://www.sengpielaudio.com/TabelleDerSchallpegel.htm :cite:`noauthor_tabelle_nodate`
und https://www.noisehelp.com/noise-level-chart.html :cite:`noauthor_noise_nodate` verwendet.

.. index:: Quellen

Bilder Quellen
^^^^^^^^^^^^^^

Alle Bilder wurden von https://pixabay.com/ heruntergeladen welche mit einer `CC0 Creative Commons`_ Lizenz dort
publiziert wurden.

.. _CC0 Creative Commons: https://pixabay.com/en/service/terms/#usage

- Stones, Meditation, Balance :cite:`tantetati_free_nodate`
- Sewing Needle, Thread, Mend :cite:`stevepb_free_nodate`
- Rocket Launch, Smoke, Rocket :cite:`wikiimages_free_nodate`
- Crash Test, Collision, 60 Km H :cite:`pixel-mixer_free_nodate`
- Riffles, Guns, Drill, Weapon, War :cite:`publicdomainpictures_free_nodate`
- Military Raptor, Jet, F-22 :cite:`skeeze_free_nodate`
- Aircraft, Airport, Departure, Start :cite:`fotoworkshop4you_free_nodate`
- Audience, Bleachers, Crowd, Game :cite:`pexels_free_nodate`
- Storm, Lightning, Weather, Nature :cite:`brinweins_free_nodate`
- Lion, Predator, Mane, Cat, Yawn :cite:`alexas_fotos_free_nodate`
- Superbike, Motorsport, Fast, Speed :cite:`sms467_free_nodate`
- Bmw, Car, Front, Sports Car, Tuned :cite:`free-photos_free_nodate`
- The Eleventh Hour, Time To Rethink :cite:`alexas_fotos_free_nodate-1`
- Adult, Bath, Beautiful, Close-Up :cite:`pexels_free_nodate-1`
- Human, Children, Girl, Talk :cite:`pezibear_free_nodate`
- Little Houses, Stone Road, Stone :cite:`free-photos_free_nodate-1`
- Bach, Forest, Water, Flow, Nature :cite:`alexas_fotos_free_nodate-2`
- Girls, Whispering, Best Friends :cite:`olichel_free_nodate`
- Autumn Leave, Japan, Nature, Maple :cite:`imagedragon_free_nodate`

.. index:: Quellen

