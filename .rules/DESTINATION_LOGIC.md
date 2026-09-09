# Destination & Path Logic Rules

This document defines the core business logic for identifying and ranking "Drift" destinations.

## 1. Top Landmarks Selection
The algorithm prioritizes high-quality landmarks within a **1km radius**:
- **Categories**: Historic Sites, Tourist Attractions, Parks, Museums, Cultural Centers, Sculptures, Libraries.
- **Selection**: The **top 10** places are selected based on a **Weighted Score**:
    - `Score = (0.3 * NormalizedPopularity) + (0.3 * NormalizedRating) + (0.4 * NormalizedProximity)`
    - `NormalizedPopularity`: Logarithmic scale of review count.
    - `NormalizedRating`: Rating out of 5.
    - `NormalizedProximity`: Inverse distance (closer is higher).

## 2. Integrated Restaurants
To provide a complete experience, the algorithm inserts dining options:
- **Categories**: Restaurants.
- **Selection**: The **top 3** restaurants are selected based on the same **Weighted Score** as landmarks.

## 3. Path Generation & Optimization
1. Combine the 10 landmarks and 3 restaurants into a set of 13 stops.
2. Generate an initial route using a **Nearest Neighbor** heuristic starting from the user's current location.
3. Refine the route using the **2-Opt algorithm** to minimize total walking distance and eliminate path crossings (TSP optimization).
