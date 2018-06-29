Auswertung
==========

Probleme
--------

Genauigkeit
^^^^^^^^^^^

Ein großes Problem ist es die verschiedene :ref:`microphone` von den unzähligen Android Geräten einheitlich ein zu stellen,
so das ein vergleichbarer Messwert herauskommt. Hierzu wurde nur ein Workaround genutzt der immer zuverlässig ist. Wie
in :ref:`quickstart` :ref:`microphone` beschrieben wird dazu ein leiser bereich benötigt um das Microphone ein zu stellen,
besser wäre hier ein Geräusch wie z.B. von einer Stimmgabel in einer exakt abgemessener Distanz zum Microphone als
Eichgeräusch zu verweden. Hinzu kommt auch noch das in einigen Geräten mehrere Microphone verbaut wurden um mit
Alogorhytmen und mehreren Audioquellen das Hintergrund rauschen raus zu filter, was in diesen fall zum teil benötigt wäre.

Unstable
^^^^^^^^

Bei Aktionen wie :ref:`position_bestimmung` und :ref:`upload_data` treten regelmäßig noch Ausnahmefehler die nicht behandelt
bzw. nicht fest gestellt wurden.


Was mit mehr Zeit umgesetzt wäre
--------------------------------

User Interface
^^^^^^^^^^^^^^

Durch die Zeitintensive integration von :ref:`opensensemap` war es mir leider nicht mehr mögich gewesen ein besseres UI
für die Graphen Darstellung zu nehmen und anderswertige App Interne Auswertungen dar zu stellen.

Permission
^^^^^^^^^^

In der aktuellen version muss noch über ``App-Info`` alle :ref:`permission` manuell aktiviert werden, der nächste step
wäre gewesen dies über die Internen funktionen von Android auf zu rufen um den User nach allen ``Permissions`` zu fragen.

Opensensemap API Integration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Anstellen von den 4 manuellen API request wäre es sinnvoller ein vollständigen API-Client zu integrieren, welche alle
Funktionen von Opensensemap integriert um so Fehler vor zu beugen und schneller & sauber neue Features hiunzufügen zu können.



