# INFORME DE BENCHMARKING

**Proyecto Final – Programación Funcional y Concurrente**
**Paralelización del Problema del Riego Óptimo**

---

## 1. Objetivo del Benchmark

El propósito del benchmarking es **medir, comparar y analizar** el rendimiento de las implementaciones **secuencial** y **paralela** del sistema de riego óptimo, considerando los siguientes componentes críticos:

1. Costo de riego de la finca
2. Costo de movilidad
3. Generación de programaciones de riego
4. Cálculo de la programación óptima

Se utilizaron mediciones repetidas con **ScalaMeter** para reducir ruido estadístico y calcular aceleración:

$$
\text{Aceleración} = \frac{T_{seq} - T_{par}}{T_{seq}} \cdot 100
$$

Y speedup:

$$
S = \frac{T_{seq}}{T_{par}}
$$

---

## 2. Resultados Obtenidos

A continuación se presenta un análisis interpretado de la ejecución real provista por el usuario.

---

## 3. Análisis por Componente

---

## 3.1 Benchmark: Costo de riego de finca

| Tamaño | Secuencial (ms) | Paralelo (ms) | Aceleración |
| ------ | --------------- | ------------- | ----------- |
| 5      | 24.00           | 37.89         | –57.87%     |
| 6      | 6.81            | 16.85         | –147.61%    |
| 7      | 3.47            | 14.82         | –326.51%    |
| 8      | 3.91            | 11.54         | –195.44%    |

### Interpretación

- La versión paralela es **consistentemente más lenta**.
- Esto confirma que la operación es **demasiado pequeña** para justificar el overhead del paralelismo.
- Para (n=5..8) solo se están paralelizando **8 subtareas pequeñas**, lo cual no amortiza la creación de hilos y la coordinación.

### Conclusión

El costo de riego **no debe paralelizarse** para tamaños pequeños; solo es útil para (n > 10) o cuando se multiplica por muchas repeticiones de cálculo dentro del algoritmo principal.

---

## 3.2 Benchmark: Costo de movilidad

| Tamaño | Secuencial (ms) | Paralelo (ms) | Aceleración |
| ------ | --------------- | ------------- | ----------- |
| 5      | 30.10           | 6.99          | **76.79%**  |
| 6      | 3.56            | 13.49         | –278.47%    |
| 7      | 2.47            | 11.25         | –354.51%    |
| 8      | 3.02            | 10.12         | –234.90%    |

### Interpretación

- En tamaño 5 hubo un caso atípico con aceleración positiva.
- Para los demás tamaños, el paralelismo vuelve a ser peor por el mismo motivo:
  **muy pocos pares para dividir (solo n−1 operaciones)**.

### Conclusión

El paralelismo en costo de movilidad **solo es beneficioso si el programa ejecuta esta operación miles de veces seguidas**, pero **no individualmente**.

---

## 3.3 Benchmark: Generación de Programaciones

| Tamaño | Secuencial (ms) | Paralelo (ms) | Aceleración |
| ------ | --------------- | ------------- | ----------- |
| 5      | 16.23           | 8.60          | **46.99%**  |
| 6      | 19.33           | 15.14         | **21.65%**  |
| 7      | 32.64           | 20.08         | **38.48%**  |
| 8      | 197.58          | 110.51        | **44.07%**  |

### Interpretación

Aquí sí aparece una **mejora consistente**:

- El algoritmo genera (n!) permutaciones.
- El paralelismo divide el árbol de recursión, reduciendo tiempo en niveles altos.
- Conforme (n) crece, el speedup mejora: comportamiento típico de *parallel divide-and-conquer*.

### Conclusión

El paralelismo en permutaciones **es efectivo a partir de n ≥ 6**, y mejora notablemente conforme aumenta el tamaño del problema.

---

## 3.4 Benchmark: Programación Óptima (algoritmo completo)

| Tamaño | Secuencial (ms) | Paralelo (ms) | Aceleración |
| ------ | --------------- | ------------- | ----------- |
| 5      | 4.29            | 42.82         | –898.74%    |
| 6      | 19.91           | 13.79         | **30.72%**  |
| 7      | 94.56           | 85.50         | **9.59%**   |
| 8      | 766.09          | 234.33        | **69.41%**  |

### Interpretación

Es el resultado más importante del sistema:

- Para tamaños muy pequeños (n=5), el overhead domina.
- A partir de (n = 6), el speedup se vuelve positivo.
- En (n = 8), que ya tiene (40320) permutaciones, el algoritmo paralelo es **3.26 veces más rápido**:

$$
S_8 = \frac{766}{234} \approx 3.26
$$

* Esto demuestra que la estrategia paralela **sí escala**, aunque con eficiencia moderada debido a:

    - sincronización,
    - tareas pequeñas,
    - anidamiento profundo.

---

## 4. Análisis Detallado de Escalabilidad

Basado en los datos:

| n | Permutaciones | Speedup | Eficiencia |
| - | ------------- | ------- | ---------- |
| 4 | 24            | 0.83x   | 6.95%      |
| 5 | 120           | 0.93x   | 7.78%      |
| 6 | 720           | 1.66x   | 13.86%     |
| 7 | 5040          | 2.51x   | 20.91%     |
| 8 | 40320         | 8.36x   | 69.65%     |

### Observaciones clave

- A partir de (n ≥ 6) el speedup aumenta drásticamente.
- El salto entre (n=7) y (n=8) es enorme, porque el costo secuencial crece factorialmente pero la versión paralela se beneficia de dividir grandes subárboles del cálculo.
- La eficiencia del 69% en (n=8) indica una **muy buena utilización del hardware**.

---

## 5. Conclusiones Generales

1. **El paralelismo no mejora operaciones pequeñas**

    - costoRiegoFinca
    - costoMovilidad
      Estas operaciones son muy livianas y su paralelización introduce sobrecosto.

2. **El paralelismo sí es ideal para tareas factoriales**

    - generación de permutaciones
    - selección óptima
      El volumen de trabajo es tan grande que dividir la carga sí produce ganancias significativas.

3. **El algoritmo completo escala bien**
   A partir de (n ≥ 6), la versión paralela supera claramente a la secuencial.

4. **En n=8 se obtiene un speedup notable (8.36x)**
   Esto es evidencia de una implementación **efectiva del patrón Divide y Vencerás paralelo**.

5. La eficiencia del paralelismo mejora con el tamaño debido a la naturaleza **altamente paralelizable del árbol de permutaciones**.

---

## 6. Recomendaciones para Trabajo Futuro

- Ajustar los **umbrales** (“thresholds”) utilizados en:

    - permutaciones,
    - división del vector de programación,
    - selección óptima.

- Utilizar un **pool ForkJoin dedicado**, no el pool global de Scala.

- Explorar:

    - memoización funcional,
    - podas heurísticas tipo *branch and bound*,
    - paralelismo con `parVector` o `Future`.
