# Taller Patrones de Diseño — Combat Simulator

Este proyecto es un **simulador de batalla por turnos** que funciona correctamente. Sin embargo, el código tiene decisiones de diseño que podrían mejorarse. Tu objetivo no es implementar métodos, sino **identificar qué patrón aplicar** en cada situación.

## Cómo ejecutar

```bash
mvn spring-boot:run
```

Abre http://localhost:8080

---

## Estructura del proyecto

```
src/main/java/com/taller/patrones/
├── domain/              # Modelo: Character, Attack, Battle
├── application/         # Casos de uso: BattleService
├── infrastructure/      # Implementaciones: CombatEngine, BattleRepository
└── interfaces/rest/     # API REST: BattleController
```

---

## Ejercicios (enfoque escenario → patrón)

### 1. Añadir un nuevo tipo de ataque

**Situación:** Quieres añadir el ataque "Meteoro" (120 de poder, tipo especial). Abres `CombatEngine` y ves que tanto `createAttack()` como `calculateDamage()` tienen un `switch` que crece con cada ataque o tipo nuevo.

**Preguntas:**
- ¿Qué problema te encuentras al añadir "Meteoro"?
    - Cada vez que añades un ataque nuevo tienes que tocar el switch de `createAttack()` y el de `calculateDamage()`, violando OCP (clase cerrada a modificación) y aumentando el riesgo de romper cosas al crecer el número de casos.
- ¿Qué pasa si mañana piden 10 ataques más?
    - El `CombatEngine` se convierte en una clase “Dios” llena de case, difícil de mantener, probar y extender; los switch se vuelven kilométricos y frágiles.
- ¿Qué patrón permitiría añadir ataques **sin modificar** `CombatEngine`?
    - Usar el patrón **Factory** para crear ataques, combinado con **Strategy** para encapsular la lógica de daño por ataque, de forma que nuevos ataques se añadan creando nuevas clases en vez de tocar `CombatEngine`.

**Pista:** Busca en `infrastructure/combat/CombatEngine.java`

---

### 2. Añadir una nueva fórmula de daño

**Situación:** Los ataques de tipo STATUS (veneno, parálisis) no deberían hacer daño directo. Pero en `calculateDamage()` el case STATUS devuelve `attacker.getAttack()` — algo no cuadra.

Además, te piden un nuevo tipo: "CRÍTICO", con fórmula `daño * 1.5` y 20% de probabilidad.

**Preguntas:**
- ¿Qué principio SOLID se viola al añadir otro `case` en el switch?
    - Se viola el Open/Closed Principle al tener que añadir otro case en el switch de calculateDamage() cada vez que aparece un tipo nuevo (STATUS, CRITICO, etc.).
- ¿Qué patrón permitiría tener fórmulas de daño intercambiables sin tocar el código existente?
    - El patrón Strategy permite tener fórmulas de daño intercambiables (una estrategia por tipo de ataque: NORMAL, SPECIAL, STATUS, CRITICO) sin modificar el código existente, solo registrando nuevas estrategias.

**Pista:** Cada tipo de ataque (NORMAL, SPECIAL, STATUS) tiene una fórmula distinta.

---

### 3. Crear personajes con muchas estadísticas

**Situación:** En `BattleService.startBattle()` creas personajes así:

```java
Character player = new Character("Héroe", 150, 25, 15, 20);
```

Ahora necesitas soportar: equipamiento, buffos temporales, clase (guerrero/mago). El constructor de `Character` empieza a tener 10+ parámetros. Algunos son opcionales.

**Preguntas:**
- ¿Qué problema tiene un constructor con muchos parámetros?
    - Aparece el “telescoping constructor”, difícil de leer, fácil de equivocarse en el orden y casi imposible saber qué representa cada número; además mezcla obligatorios y opcionales sin claridad.
- ¿Cómo harías para que `new Character(...)` sea legible cuando hay valores por defecto?
    - Utilizar un builder fluido, por ejemplo new CharacterBuilder().withName("Héroe").withHp(150).withAttack(25)...build(), de modo que se vea qué se está seteando y qué queda por defecto.
- ¿Qué patrón permite construir objetos complejos paso a paso?
    - El patrón Builder permite construir objetos complejos paso a paso, con parámetros opcionales y valores por defecto de forma legible.

**Pista:** Mira cómo se crean los personajes en `BattleService` y en el endpoint `/start/external`.

---

### 4. Un único almacén de batallas

**Situación:** `BattleRepository` usa un `Map` estático para que funcione. Pero `BattleService` hace `new BattleRepository()` cada vez. Si otro equipo crea un `TournamentService` que también hace `new BattleRepository()`, ¿compartirían las batallas?

**Preguntas:**
- ¿Qué pasaría si dos clases crean su propio `BattleRepository` sin el `static`?
    - Cada una tendría su propio Map interno distinto, por lo que las batallas guardadas en uno no serían visibles en el otro; no habría almacén compartido real.
- ¿Cómo asegurar que **toda la aplicación** use la misma instancia de almacenamiento?
    - Centralizar el acceso a BattleRepository mediante un punto de acceso global que devuelva siempre la misma instancia en toda la aplicación.
- ¿Qué patrón garantiza una única instancia de una clase?
    - El patrón Singleton garantiza que solo exista una instancia de BattleRepository y que todos los servicios usen la misma.

**Pista:** `infrastructure/persistence/BattleRepository.java`

---

### 5. Recibir datos de un API externo

**Situación:** El endpoint `POST /api/battle/start/external` recibe JSON con campos `fighter1_hp`, `fighter1_atk`, `fighter2_name`, etc. El controller hace el mapeo manual a `Character` y `Battle`.

Mañana llega otro proveedor con formato distinto: `player.health`, `player.attack`, `enemy.health`...

**Preguntas:**
- ¿Qué problema hay en poner la lógica de conversión en el controller?
    - El controller se ensucia con lógica de mapeo, viola SRP (además de controlar HTTP, hace transformación de modelos), se vuelve difícil de mantener y cada nuevo proveedor obliga a modificar el controller.
- ¿Cómo aislar la conversión "formato externo → nuestro dominio" para no ensuciar el controller?
    - Crear componentes dedicados a esa transformación (mappers/adapters) que reciban el JSON externo o un DTO y devuelvan objetos de dominio (Character, Battle), manteniendo el controller delgado.
- ¿Qué patrón permite que un objeto "adaptado" se use como si fuera uno de los nuestros?
    - El patrón Adapter permite envolver los datos del proveedor externo en un objeto que se comporta como el nuestro, de modo que el resto de la aplicación use una interfaz de dominio estable.

**Pista:** `interfaces/rest/BattleController.java` — método `startBattleFromExternal`

---

### 6. Notificar cuando ocurre daño

**Situación:** Necesitas:
- Enviar un evento a un sistema de analytics cada vez que hay daño
- Escribir en un log de auditoría
- Actualizar estadísticas en tiempo real

Ahora mismo solo existe `battle.log()`. Tendrías que añadir código en `BattleService.applyDamage()` para cada uno de estos casos.

**Preguntas:**
- ¿Qué pasa si añades 5 "suscriptores" más? ¿Cuántas líneas tocarías en `applyDamage()`?
  - Habrá que tocar ese método cada vez que aparezca un nuevo “consumidor” del evento de daño (analytics, log, métricas, achievements…), violando OCP y llenando el método de dependencias.
- ¿Cómo desacoplar "ejecutar ataque" de "notificar a quien le interese"?
  - Se necesita que `BattleService` solo “emita” un evento de daño (por ejemplo, `damageDone(battle, attacker, target, amount)`) y que otros objetos se suscriban a ese evento sin que `applyDamage()` los conozca ni invoque explícitamente.
- ¿Qué patrón permite que varios objetos reaccionen a un evento sin que el emisor los conozca?
  - El patrón adecuado es Observer: `BattleService` actúa como subject y mantiene una lista de observadores (analytics, auditoría, estadísticas), notificándolos cuando ocurre daño mediante una interfaz común.

**Pista:** El método `applyDamage` en `BattleService` es el único que sabe cuándo hay daño.

---

### 7. Deshacer el último ataque

**Situación:** Quieren la funcionalidad "Deshacer" — revertir el último ataque ejecutado.

Ahora el ataque se ejecuta directamente en `applyDamage()`. No hay registro de "qué se hizo".

**Preguntas:**
- ¿Qué tendrías que cambiar para poder "deshacer"?
  - Habría que registrar qué ataque se ejecutó y qué cambios produjo (daño realizado, puntos de vida anteriores, estado aplicado, etc.) en vez de aplicar el daño “a pelo” dentro del método, además de guardar ese registro en una lista de historial.
- ¿Cómo encapsular una acción (ataque) para poder ejecutarla, guardarla y revertirla?
  - La acción “ejecutar ataque” se puede encapsular en un objeto que tenga métodos como `execute()` y `undo()`, donde `execute()` aplica el daño y `undo()` revierte los cambios (restaurar HP, quitar estados, deshacer cambio de turno si procede).
- ¿Qué patrón trata las acciones como objetos de primera clase?
  - El patrón adecuado es Command, que trata las acciones como objetos de primera clase, pudiendo loguearlas, guardarlas en una pila para undo/redo y ejecutarlas de forma desacoplada del código que las invoca.

**Pista:** La lógica del ataque está en `BattleService.applyDamage()`.

---

### 8. Simplificar la API del combate

**Situación:** Para ejecutar un ataque, el controller llama a `battleService.executePlayerAttack()` o `executeEnemyAttack()`, que a su vez usa `CombatEngine`, aplica daño, cambia turno, etc. Un cliente externo que quiera integrarse tendría que conocer `BattleService`, `CombatEngine`, `BattleRepository`...

**Preguntas:**
- ¿Qué problema hay en exponer muchos detalles internos a quien solo quiere "hacer un ataque"?
  - Exponer `BattleService`, `CombatEngine`, `BattleRepository`, etc. obliga a los clientes a conocer muchos detalles internos del sistema de combate, aumenta el acoplamiento y hace más frágil la integración (cualquier cambio interno puede romper a los clientes).
- ¿Qué patrón ofrece una interfaz simple que oculta la complejidad del subsistema?
  - El patrón adecuado es Facade: crear una clase (por ejemplo, BattleFacade o CombatApi) con métodos de alto nivel como executeAttack(battleId, attackerId, attackId) que internamente coordina BattleService, CombatEngine y el repositorio, ocultando la complejidad.

**Pista:** Piensa en qué necesita saber un cliente para ejecutar un ataque.

---

### 9. Ataques compuestos (combo)

**Situación:** Quieres un ataque "Combo Triple" que ejecuta Tackle + Slash + Fireball en secuencia.

Ahora cada ataque es independiente. No hay forma de agrupar varios.

**Preguntas:**
- ¿Cómo representar "un ataque que son varios ataques"?
  - Mediante un objeto que implemente la misma interfaz que un ataque normal (`Attack`) pero que internamente contenga una colección de otros ataques y los ejecute en secuencia (Tackle, luego Slash, luego Fireball).
- ¿Qué patrón permite tratar un grupo de objetos igual que un objeto individual?
  - El patrón adecuado es Composite: defines `Attack` como componente, las implementaciones simples (Tackle, Slash, Fireball) como hojas, y `ComboAttack` como compuesto que tiene una lista de `Attack` y, al ejecutarse, delega en cada uno; desde el punto de vista de `CombatEngine`, un combo es “un ataque más”.

**Pista:** `Attack` es una unidad. ¿Cómo hacer que varios `Attack` se comporten como uno?

---

## Resumen: Patrones del taller

| Patrón   | Situación típica                                      |
|----------|--------------------------------------------------------|
| Singleton| Una única instancia en toda la aplicación              |
| Factory  | Crear objetos sin conocer la clase concreta           |
| Builder  | Construir objetos con muchos parámetros opcionales    |
| Adapter  | Usar una interfaz externa como si fuera la nuestra    |
| Strategy | Algoritmos intercambiables (fórmulas de daño)        |
| Observer | Notificar a varios sin acoplar emisor y receptores     |
| Command  | Encapsular acciones para ejecutar, deshacer, encolar  |
| Facade   | Interfaz simple sobre un subsistema complejo          |
| Composite| Tratar grupos como elementos individuales             |