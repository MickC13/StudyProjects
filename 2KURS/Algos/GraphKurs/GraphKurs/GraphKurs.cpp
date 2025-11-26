#include <iostream>
#include <vector>
#include <algorithm>
#include <functional>
#include <random>

class Graph {
private:
    int vertexCount;
    std::vector<std::vector<int>> adjacencyList;
    std::string name;

public:
    // Constructors
    Graph() : vertexCount(0), name("Unnamed") {}

    explicit Graph(int n, const std::string& graphName = "Unnamed")
        : vertexCount(n), name(graphName) {
        adjacencyList.resize(n);
    }

    // Access methods
    const std::string& getName() const { return name; }
    void setName(const std::string& newName) { name = newName; }

    int getVertexCount() const { return vertexCount; }

    const std::vector<std::vector<int>>& getAdjacencyList() const {
        return adjacencyList;
    }

    // Add edge
    void addEdge(int u, int v) {
        if (u >= 0 && u < vertexCount && v >= 0 && v < vertexCount && u != v) {
            // Check if edge already exists
            if (std::find(adjacencyList[u].begin(), adjacencyList[u].end(), v) == adjacencyList[u].end()) {
                adjacencyList[u].push_back(v);
                adjacencyList[v].push_back(u);
            }
        }
    }

    // Remove edge
    void removeEdge(int u, int v) {
        if (u >= 0 && u < vertexCount && v >= 0 && v < vertexCount) {
            auto it = std::find(adjacencyList[u].begin(), adjacencyList[u].end(), v);
            if (it != adjacencyList[u].end()) {
                adjacencyList[u].erase(it);
            }

            it = std::find(adjacencyList[v].begin(), adjacencyList[v].end(), u);
            if (it != adjacencyList[v].end()) {
                adjacencyList[v].erase(it);
            }
        }
    }

    // Get vertex degree
    int getDegree(int vertex) const {
        if (vertex >= 0 && vertex < vertexCount) {
            return adjacencyList[vertex].size();
        }
        return 0;
    }

    // Clear graph
    void clear() {
        vertexCount = 0;
        adjacencyList.clear();
    }

    // Resize graph
    void resize(int newSize) {
        if (newSize < 0) return;
        vertexCount = newSize;
        adjacencyList.resize(newSize);
    }

    // Print graph information
    void printInfo() const {
        std::cout << "Graph: " << name << "\n";
        std::cout << "Number of vertices: " << vertexCount << "\n";

        std::cout << "Adjacency lists:\n";
        for (int i = 0; i < vertexCount; ++i) {
            std::cout << i << " (degree " << getDegree(i) << "): ";
            for (int neighbor : adjacencyList[i]) {
                std::cout << neighbor << " ";
            }
            std::cout << "\n";
        }
    }

    // Метод для поиска точек сочленения (ваш вариант 27)
    std::vector<int> findArticulationPoints() const {
        std::vector<int> articulationPoints;
        if (vertexCount == 0) return articulationPoints;

        std::vector<int> discoveryTime(vertexCount, -1);
        std::vector<int> lowTime(vertexCount, -1);
        std::vector<int> parent(vertexCount, -1);
        std::vector<bool> visited(vertexCount, false);

        int time = 0;

        // DFS для поиска точек сочленения
        std::function<void(int, std::vector<bool>&)> dfs =
            [&](int u, std::vector<bool>& visited) {
            visited[u] = true;
            discoveryTime[u] = lowTime[u] = ++time;
            int children = 0;

            for (int v : adjacencyList[u]) {
                if (!visited[v]) {
                    children++;
                    parent[v] = u;
                    dfs(v, visited);

                    // Обновляем lowTime для u
                    lowTime[u] = std::min(lowTime[u], lowTime[v]);

                    // Проверяем условия точки сочленения
                    // Условие 1: u - корень и имеет >= 2 детей
                    if (parent[u] == -1 && children >= 2) {
                        if (std::find(articulationPoints.begin(),
                            articulationPoints.end(), u) == articulationPoints.end()) {
                            articulationPoints.push_back(u);
                        }
                    }
                    // Условие 2: u не корень и lowTime[v] >= discoveryTime[u]
                    if (parent[u] != -1 && lowTime[v] >= discoveryTime[u]) {
                        if (std::find(articulationPoints.begin(),
                            articulationPoints.end(), u) == articulationPoints.end()) {
                            articulationPoints.push_back(u);
                        }
                    }
                }
                // Обновляем lowTime для u через обратное ребро
                else if (v != parent[u]) {
                    lowTime[u] = std::min(lowTime[u], discoveryTime[v]);
                }
            }
        };

        // Запускаем DFS для всех компонент связности
        for (int i = 0; i < vertexCount; ++i) {
            if (!visited[i]) {
                dfs(i, visited);
            }
        }

        return articulationPoints;
    }

    // Метод для демонстрации точек сочленения
    void printArticulationPoints() const {
        auto points = findArticulationPoints();
        std::cout << "Articulation points for graph '" << name << "': ";
        if (points.empty()) {
            std::cout << "none (graph is biconnected)";
        }
        else {
            for (int point : points) {
                std::cout << point << " ";
            }
        }
        std::cout << "\n";
    }
};

class GraphGenerator {
private:
    std::random_device rd;
    std::mt19937 gen;

public:
    GraphGenerator() : gen(rd()) {}

    // Generate random graph
    Graph generateRandomGraph(int vertexCount, double edgeProbability, const std::string& name = "RandomGraph") {
        Graph graph(vertexCount, name);
        std::uniform_real_distribution<double> dist(0.0, 1.0);

        for (int i = 0; i < vertexCount; ++i) {
            for (int j = i + 1; j < vertexCount; ++j) {
                if (dist(gen) < edgeProbability) {
                    graph.addEdge(i, j);
                }
            }
        }

        return graph;
    }

    // Generate complete graph
    Graph generateCompleteGraph(int vertexCount, const std::string& name = "CompleteGraph") {
        Graph graph(vertexCount, name);

        for (int i = 0; i < vertexCount; ++i) {
            for (int j = i + 1; j < vertexCount; ++j) {
                graph.addEdge(i, j);
            }
        }

        return graph;
    }

    // Generate cycle graph
    Graph generateCycleGraph(int vertexCount, const std::string& name = "CycleGraph") {
        Graph graph(vertexCount, name);

        for (int i = 0; i < vertexCount; ++i) {
            graph.addEdge(i, (i + 1) % vertexCount);
        }

        return graph;
    }

    // Generate star graph
    Graph generateStarGraph(int vertexCount, const std::string& name = "StarGraph") {
        Graph graph(vertexCount, name);

        for (int i = 1; i < vertexCount; ++i) {
            graph.addEdge(0, i);
        }

        return graph;
    }
};

// Демонстрация работы алгоритма
void demonstrateArticulationPoints() {
    std::cout << "=== ARTICULATION POINTS DEMONSTRATION (Variant 27) ===\n\n";

    GraphGenerator generator;

    // Пример 1: Граф с точками сочленения
    std::cout << "Example 1: Graph with articulation points\n";
    Graph g1(5, "Graph1");
    g1.addEdge(0, 1);
    g1.addEdge(1, 2);
    g1.addEdge(2, 0);
    g1.addEdge(1, 3);
    g1.addEdge(3, 4);

    g1.printInfo();
    g1.printArticulationPoints();

    std::cout << "\n" << std::string(50, '-') << "\n\n";

    // Пример 2: Двусвязный граф (без точек сочленения)
    std::cout << "Example 2: Biconnected graph (no articulation points)\n";
    Graph g2(4, "Graph2");
    g2.addEdge(0, 1);
    g2.addEdge(1, 2);
    g2.addEdge(2, 3);
    g2.addEdge(3, 0);
    g2.addEdge(0, 2);

    g2.printInfo();
    g2.printArticulationPoints();

    std::cout << "\n" << std::string(50, '-') << "\n\n";

    // Пример 3: Звезда (центральная вершина - точка сочленения)
    std::cout << "Example 3: Star graph (center is articulation point)\n";
    Graph g3 = generator.generateStarGraph(5, "StarGraph");

    g3.printInfo();
    g3.printArticulationPoints();

    std::cout << "\n" << std::string(50, '-') << "\n\n";

    // Пример 4: Дерево (все внутренние вершины - точки сочленения)
    std::cout << "Example 4: Tree (all internal vertices are articulation points)\n";
    Graph g4(7, "Tree");
    g4.addEdge(0, 1);
    g4.addEdge(1, 2);
    g4.addEdge(1, 3);
    g4.addEdge(2, 4);
    g4.addEdge(2, 5);
    g4.addEdge(3, 6);

    g4.printInfo();
    g4.printArticulationPoints();
}

// Интерактивный режим
void interactiveMode() {
    std::cout << "=== INTERACTIVE MODE ===\n";
    std::cout << "1 - Generate random graph\n";
    std::cout << "2 - Create graph manually\n";
    std::cout << "3 - Show demonstration\n";
    std::cout << "0 - Exit\n";

    int choice;
    Graph graph;
    GraphGenerator generator;

    while (true) {
        std::cout << "\nSelect action: ";
        std::cin >> choice;

        switch (choice) {
        case 1: {
            int size;
            double probability;
            std::cout << "Number of vertices: ";
            std::cin >> size;
            std::cout << "Edge probability (0.0-1.0): ";
            std::cin >> probability;

            graph = generator.generateRandomGraph(size, probability, "RandomGraph");

            graph.printInfo();
            graph.printArticulationPoints();
            break;
        }
        case 2: {
            int size;
            std::cout << "Number of vertices: ";
            std::cin >> size;

            graph = Graph(size, "ManualGraph");

            std::cout << "Enter edges (vertex1 vertex2), enter -1 -1 to finish:\n";
            int u, v;
            while (true) {
                std::cin >> u >> v;
                if (u == -1 || v == -1) break;
                if (u < 0 || u >= size || v < 0 || v >= size) {
                    std::cout << "Invalid vertex! Vertices must be between 0 and " << size - 1 << "\n";
                    continue;
                }
                graph.addEdge(u, v);
            }

            graph.printInfo();
            graph.printArticulationPoints();
            break;
        }
        case 3:
            demonstrateArticulationPoints();
            break;
        case 0:
            return;
        default:
            std::cout << "Invalid choice!\n";
        }
    }
}

int main() {
    std::cout << "Graph Articulation Points Finder - Variant 27\n";
    std::cout << "==============================================\n\n";

    // Автоматическая демонстрация
    demonstrateArticulationPoints();

    // Интерактивный режим
    std::cout << "\nSwitch to interactive mode? (y/n): ";
    char response;
    std::cin >> response;

    if (response == 'y' || response == 'Y') {
        interactiveMode();
    }

    std::cout << "Program finished.\n";
    return 0;
}