# Registro de prompts

- Integrantes:
- Herramienta(s) de IA utilizada(s):

## Prompt 1
Herramienta utilizada: Claude Code (extensión de VS Code).

Contexto: Estás trabajando en un repositorio base que ya tiene la estructura del proyecto y los datos en la carpeta datos/. Solo puedes usar como insumo (a) los requerimientos que te doy abajo y (b) los archivos CSV de la carpeta datos/. No uses ninguna otra fuente de información sobre el problema.

REGLAS OBLIGATORIAS
1. No sugieras mejoras, cambios, alternativas ni requerimientos nuevos. Implementa los requerimientos exactamente como están escritos, aunque te parezca que hay una mejor solución. No agregues funcionalidades que no se piden.
2. Si un requerimiento deja abierto un detalle de implementación, elige la opción más simple que lo cumpla literalmente, explícala en un comentario en el código y no la presentes como sugerencia.
3. Trabajaremos feature por feature. En este prompt implementa ÚNICAMENTE el Feature F1. No implementes nada de otros features ni dejes código adelantado para ellos.
4. Respeta la estructura de carpetas, paquetes y convenciones que ya existen en el repositorio base.

RESTRICCIONES DE IMPLEMENTACIÓN (obligatorias)
- Lenguaje: Java 17 o superior.
- Debes implementar tus propias estructuras de datos y algoritmos.
- Permitido: arreglos (por ejemplo int[] o Sala[]) y String.
- Prohibido: cualquier colección de java.util (ArrayList, LinkedList, HashMap, HashSet, TreeMap, TreeSet, PriorityQueue, Stack, ArrayDeque, entre otras), así como Arrays.sort, Collections.sort, List.sort y sorted() de Streams.
- Prohibido: bibliotecas que calculen caminos más cortos o que implementen grafos.
- Las pruebas unitarias pueden usar además JUnit (ya configurado en el repositorio).

REQUERIMIENTOS DEL FEATURE F1 (tal como fueron presentados; no los modifiques)
F1. Estructura: Árbol B+. Complejidad: O(log n). Alternativa descartada: Árbol AVL. La razón fue para aprovechar en el B+ la característica del orden de rango, ya que AVL, cuando se ordena, no respeta el orden original. También era importante en el B+ que cada hoja está enlazada, permitiendo contar los ocupantes.
Requerimientos:
- Que la estructura sea capaz de respetar el orden de rango de sala -> ala -> edificio.
- Que el sistema sea capaz de listar su contenido, actualizarlo y enumerar sus ocupantes.

METODOLOGÍA: TDD
Desarrolla con TDD: primero escribe las pruebas JUnit, verifica que fallen, luego implementa el código mínimo para que pasen y finalmente refactoriza. Por cada requerimiento funcional incluye como mínimo:
- Una prueba que compare el valor exacto esperado (assertEquals con el valor concreto calculado a partir de los CSV de datos/).
- Casos límite (por ejemplo: un ala vacía, empates, árbol con un solo elemento).
- Entradas inválidas (por ejemplo: una sala o ala inexistente, o un dato mal formado en el CSV), verificando el error o el resultado esperado.
Las pruebas no deben limitarse a verificar verdadero/falso.

EJECUCIÓN
Agrega al programa principal la ejecución de F1 cargando los datos desde datos/ y mostrando: personas por ala y en todo el edificio.

REGISTRO DE PROMPTS
Si no existe, crea el archivo PROMPTS.md en la raíz del repositorio. Agrega este prompt completo, textual y sin resumir, como "Prompt 1", indicando la herramienta utilizada (Claude Code, extensión de VS Code). Cada prompt que te envíe después también debe agregarse textualmente y en orden.

AL TERMINAR
Ejecuta todas las pruebas y muéstrame el resultado. Dame un resumen breve de los archivos creados o modificados y de qué requerimientos de F1 quedaron implementados. No propongas mejoras ni pasos adicionales. Espera mi siguiente prompt para el Feature F2.

## Mensaje intermedio después del Prompt 1
por favor no utilicez comimits sin mi permiso o avisarme

## Prompt 2
Herramienta utilizada: Claude Code (extensión de VS Code).

Continuamos con el mismo proyecto. Siguen aplicando todas las reglas y restricciones del Prompt 1. Las repito porque son obligatorias:

REGLAS OBLIGATORIAS
1. No sugieras mejoras, cambios, alternativas ni requerimientos nuevos. Implementa los requerimientos exactamente como están escritos, aunque te parezca que hay una mejor solución. No agregues funcionalidades que no se piden.
2. Si un requerimiento deja abierto un detalle de implementación, elige la opción más simple que lo cumpla literalmente, explícala en un comentario en el código y no la presentes como sugerencia.
3. En este prompt implementa ÚNICAMENTE el Feature F2. No modifiques el comportamiento de F1 salvo lo estrictamente necesario para reutilizar sus datos (por ejemplo, obtener la lista de salas ya cargadas). No implementes nada de F3.
4. Respeta la estructura de carpetas, paquetes y convenciones que ya existen en el repositorio.

RESTRICCIONES DE IMPLEMENTACIÓN (obligatorias)
- Lenguaje: Java 17 o superior.
- Debes implementar tus propias estructuras de datos y algoritmos.
- Permitido: arreglos (por ejemplo int[] o Sala[]) y String.
- Prohibido: cualquier colección de java.util (ArrayList, LinkedList, HashMap, HashSet, TreeMap, TreeSet, PriorityQueue, Stack, ArrayDeque, entre otras), así como Arrays.sort, Collections.sort, List.sort y sorted() de Streams.
- Prohibido: bibliotecas que calculen caminos más cortos o que implementen grafos.
- Las pruebas unitarias pueden usar además JUnit (ya configurado en el repositorio).

REQUERIMIENTOS DEL FEATURE F2 (tal como fueron presentados; no los modifiques)
F2. Algoritmo: Quicksort. Complejidad: n log n. Otra opción a tomar en cuenta es Bubblesort, por ejemplo; sin embargo, ya que el sistema requiere rapidez, se elige Quicksort, ya que es su cualidad más importante en este caso.
Requerimientos:
- Que sea capaz de enlistar 400 salas en orden de mayor a menor ocupante.
- Que sea un sistema eficiente, ya que debe hacer los cálculos en 1 segundo.

Implementa Quicksort a mano sobre un arreglo de salas, ordenando de mayor a menor cantidad de ocupantes. Como los requerimientos no definen un criterio de desempate, no agregues uno.

METODOLOGÍA: TDD
Desarrolla con TDD: primero escribe las pruebas JUnit, verifica que fallen, luego implementa el código mínimo para que pasen y finalmente refactoriza. Por cada requerimiento incluye como mínimo:
- Una prueba que compare el valor exacto esperado (assertEquals con el orden y los valores concretos calculados a partir de los CSV de datos/).
- Casos límite: arreglo vacío, una sola sala, salas con la misma cantidad de ocupantes (empates) y un arreglo ya ordenado. En los empates, como no hay criterio de desempate definido, verifica que la secuencia de ocupantes quede de mayor a menor y que no se pierda ni se duplique ninguna sala.
- Entradas inválidas: arreglo nulo, sala con ocupantes negativos o dato mal formado en el CSV, verificando el error o el resultado esperado.
- Para el requerimiento de eficiencia: una prueba que genere 400 salas y verifique, con assertTimeout de JUnit, que el ordenamiento termina en menos de 1 segundo y que el resultado queda en orden de mayor a menor.
Las pruebas no deben limitarse a verificar verdadero/falso.

EJECUCIÓN
Agrega al programa principal la ejecución de F2 con los datos de datos/, mostrando el reporte de salas de mayor a menor ocupantes.

REGISTRO DE PROMPTS
Agrega este prompt completo, textual y sin resumir, a PROMPTS.md como "Prompt 2", indicando la herramienta utilizada (Claude Code, extensión de VS Code). Verifica que el Prompt 1 y cualquier respuesta intermedia que te haya enviado ya estén registrados en orden.

AL TERMINAR
Ejecuta todas las pruebas (incluidas las de F1) y muéstrame el resultado. Dame un resumen breve de los archivos creados o modificados y de qué requerimientos de F2 quedaron implementados. No propongas mejoras ni pasos adicionales. Espera mi siguiente prompt para el Feature F3.

no realices commits sin mi permiso, por favor.
