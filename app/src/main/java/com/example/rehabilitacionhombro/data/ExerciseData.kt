// Fichero: app/src/main/java/com/example/rehabilitacionhombro/data/ExerciseData.kt
package com.example.rehabilitacionhombro.data

object ExerciseData {
    // Esta función proporciona la lista de ejercicios por defecto
    fun getDefaultExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 1,
                phase = "Fase 1: Preparación y Calentamiento",
                title = "Péndulo de Codman",
                imageResName = "pendulo_de_codman",
                description = "Ponte de pie, inclina tu tronco hacia adelante y apoya tu brazo sano en una mesa. Deja que tu brazo lesionado cuelgue relajado. Usa el impulso de tu cuerpo para generar un movimiento suave: adelante y atrás, de lado a lado y en pequeños círculos.",
                muscle = "Manguito rotador (pasivo)",
                // **CAMBIADO:** Se usan los nuevos campos
                sets = 1,
                reps = 0, // No aplica, lo dejaremos en 0
                isTimed = true,
                duration = 180
            ),
            Exercise(
                id = 2,
                phase = "Fase 1: Preparación y Calentamiento",
                title = "Movilidad Activa-Asistida",
                imageResName = "movilidad_asistida",
                description = "Túmbate boca arriba. Con tu mano sana, sujeta la muñeca o el codo de tu brazo lesionado. Guía suavemente tu brazo hacia el techo (flexión) y luego separándolo de tu cuerpo (abducción), hasta tu límite sin dolor.",
                muscle = "Deltoides y manguito rotador",
                sets = 10,
                reps = 15,
                isTimed = false
            ),
            Exercise(
                id = 3,
                phase = "Fase 2: Activación",
                title = "Neurodinamia (Deslizamiento Neural)",
                imageResName = "neurodinamia_plexo_braquial",
                description = "Siéntate con la espalda recta. Inclina LENTAMENTE la cabeza hacia el lado contrario del hombro lesionado. Mantén esa posición solo 5 segundos (tensión muy leve, no dolor) y vuelve al centro.",
                muscle = "Plexo braquial (nervios)",
                sets = 1,
                reps = 5,
                isTimed = false
            ),
            Exercise(
                id = 4,
                phase = "Fase 2: Activación",
                title = "Isométricos de Rotación",
                imageResName = "rotacion_externa_e_interna",
                description = "Empuja suavemente contra el marco de una puerta para activar los músculos de rotación interna y externa sin mover el brazo. Mantén la presión 5 segundos.",
                muscle = "Manguito rotador",
                sets = 2,
                reps = 10,
                isTimed = false
            ),
            Exercise(
                id = 5,
                phase = "Fase 3: Fortalecimiento Escapular",
                title = "Retracción y Protracción en Cuadrupedia",
                imageResName = "retraccion_y_protraccion_escapular_cuadrupedia",
                description = "En cuatro patas, sin doblar los codos, hunde el pecho juntando las escápulas (retracción) y luego empuja hacia arriba separándolas (protracción).",
                muscle = "Romboides y serrato anterior",
                sets = 2,
                reps = 15,
                isTimed = false
            ),
            Exercise(
                id = 6,
                phase = "Fase 3: Fortalecimiento Escapular",
                title = "Pájaros con Apoyo",
                imageResName = "pajaros_apoyado",
                description = "Tumbado boca abajo sobre un banco o cama, levanta los brazos hacia los lados con los codos ligeramente doblados, juntando las escápulas.",
                muscle = "Deltoides posterior y trapecio",
                sets = 2,
                reps = 12,
                isTimed = false
            ),
            Exercise(
                id = 7,
                phase = "Fase 4: Fortalecimiento con Banda",
                title = "Remo con Banda",
                imageResName = "remo_con_banda_de_resistencia",
                description = "Sentado, con la banda en los pies, tira de ella llevando los codos hacia atrás y juntando las escápulas.",
                muscle = "Dorsales y romboides",
                sets = 3,
                reps = 12,
                isTimed = false
            ),
            Exercise(
                id = 8,
                phase = "Fase 4: Fortalecimiento con Banda",
                title = "Rotación Externa con Banda",
                imageResName = "rotacion_externa_hombro_banda",
                description = "De pie, con el codo pegado al costado, gira el antebrazo hacia afuera contra la resistencia de la banda. El codo no se separa del cuerpo.",
                muscle = "Infraespinoso y redondo menor",
                sets = 3,
                reps = 10,
                isTimed = false
            ),
            Exercise(
                id = 9,
                phase = "Fase 4: Fortalecimiento con Banda",
                title = "Rotación Interna con Banda",
                imageResName = "rotacion_interna_hombro_banda",
                description = "Similar al anterior, pero ahora gira el antebrazo hacia adentro, llevando la mano hacia tu abdomen.",
                muscle = "Subescapular",
                sets = 3,
                reps = 10,
                isTimed = false
            ),
            Exercise(
                id = 10,
                phase = "Fase 4: Fortalecimiento con Banda",
                title = "Serratus Punch con Banda",
                imageResName = "serratus_punch_con_banda",
                description = "Con la banda enganchada detrás de ti, estira el brazo hacia adelante. Desde ahí, sin doblar el codo, empuja un poco más, como si quisieras alcanzar algo.",
                muscle = "Serrato anterior",
                sets = 3,
                reps = 10,
                isTimed = false
            ),
            Exercise(
                id = 11,
                phase = "Fase 5: Vuelta a la Calma",
                title = "Estiramiento de Pectorales",
                imageResName = "estiramiento_pectoral",
                description = "Apoya el antebrazo en el marco de una puerta, con el codo a 90°. Da un pequeño paso adelante hasta sentir un estiramiento suave en el pecho.",
                muscle = "Pectoral",
                sets = 1,
                reps = 0,
                isTimed = true,
                duration = 30,
                rest = 10
            ),
            Exercise(
                id = 12,
                phase = "Fase 5: Vuelta a la Calma",
                title = "Estiramiento de Cápsula Posterior",
                imageResName = "estiramientos_hombros",
                description = "Lleva tu brazo lesionado estirado a través de tu pecho. Con tu mano sana, tira suavemente desde el codo hacia tu cuerpo.",
                muscle = "Deltoides posterior",
                sets = 1,
                reps = 0,
                isTimed = true,
                duration = 30,
                rest = 10
            )
        )
    }
}