[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/h71fa0_C)
# Asignación: Proyecto Final — El Problema del Riego Óptimo

**Fecha:** 08/12/2025
**Curso:** Fundamentos de Programación Funcional y Concurrente
**Grupo:** 51

---

## 👥 Integrantes del Grupo

| Nombre Completo                       | Código  | Rol          | Correo Electrónico                                                                              |
| ------------------------------------- | ------- | ------------ | ----------------------------------------------------------------------------------------------- |
| Jaider Bermúdez Girón                 | 2569453 | Colaborador  | [jaider.bermudez@correounivalle.edu.co](mailto:jaider.bermudez@correounivalle.edu.co)           |
| Valentina Betancourt Caicedo          | 2459411 | Colaboradora | [betancourt.valentina@correounivalle.edu.co](mailto:betancourt.valentina@correounivalle.edu.co) |
| Juan Esteban Pereira Neira            | 2569459 | Colaborador  | [juan.pereira.neira@correounivalle.edu.co](mailto:juan.pereira.neira@correounivalle.edu.co)     |
| Scarllys del Valle Vallecilla Caicedo | 2459608 | Colaboradora | [scarllys.vallecilla@correounivalle.edu.co](mailto:scarllys.vallecilla@correounivalle.edu.co)   |

---

## 📌 Descripción de la Asignación

Este proyecto desarrolla una solución completa al **Problema del Riego Óptimo**, en el cual un robot debe regar un conjunto de tablones agrícolas minimizando el costo total:

- **Costo por tiempo sin regar**
- **Costo de movilidad del robot entre tablones**

Para resolverlo, se exploran todas las posibles permutaciones de riego y se selecciona aquella cuyo costo total es mínimo. El trabajo también incluye:

- Implementación funcional, inmutable y recursiva del algoritmo secuencial
- Versión paralela basada en el patrón **Divide y Vencerás**
- Análisis de corrección formal
- Estudio de rendimiento mediante **benchmarking y speedup**

---

## 🎯 Objetivos del Proyecto

1. Implementar una solución **funcional pura** (inmutable, recursiva y basada en árboles).
2. Explorar las $n!$ permutaciones para obtener la solución óptima.
3. Paralelizar:
   - la generación de permutaciones
   - el cálculo de costos
   - la búsqueda de la mejor solución
      utilizando `parallel`, `task` y técnicas de división y conquista.
4. Argumentar formalmente la **corrección** de todas las funciones.
5. Evaluar el rendimiento secuencial vs paralelo mediante **benchmarking**.

---

## ⚙️ Estructura del Proyecto

La organización separa claramente la lógica funcional secuencial, la lógica paralela y el análisis experimental:

- **`taller/RiegoOptimo.scala`:** Implementación **secuencial** del algoritmo de riego óptimo usando recursión, estructuras inmutables y evaluación funcional.

- **`taller/RiegoOptimoPar.scala`:** Versión **paralela** de las funciones principales (`costoPar`, `buscarOptimoPar`, permutaciones paralelas), usando `parallel` y `task`.

- **`taller/RiegoOptimoTest.scala`:** Pruebas unitarias que validan la corrección de las funciones secuenciales.

- **`taller/RiegoOptimoParTest.scala`:** Pruebas de consistencia que verifican que las funciones paralelas retornan los mismos resultados que la versión secuencial.

- **`taller/Benchmarking.scala`:** Programa principal para medir los tiempos de ejecución, calcular **speedup**, eficiencia, y generar las tablas comparativas.
