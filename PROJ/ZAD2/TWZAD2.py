import time
import sys
import torch
import networkx as nx
import matplotlib.pyplot as plt
from copy import deepcopy


class Operation:
    def __init__(self, op_type, j, i, k=None) -> None:
        self.op_type = op_type
        self.j = j
        self.i = i
        self.k = k

    def __str__(self):
        return f"A{self.j}{self.i}" if self.op_type == "A" else f"{self.op_type}{self.j}{self.i}{self.k}"

    def __repr__(self):
        return f"A{self.j}{self.i}" if self.op_type == "A" else f"{self.op_type}{self.j}{self.i}{self.k}"

    def __eq__(self, other):
        if not isinstance(other, Operation):
            return False
        return other.op_type == self.op_type and other.j == self.j and other.i == self.i and other.k == self.k

    def __hash__(self):
        return hash((self.op_type, self.j, self.i, self.k))


def gen_sigma(N):
    sigma = set()

    for i in range(N):
        for j in range(i+1, N):
            sigma.add(Operation("A", j, i))
            for k in range(i, N+1):
                sigma.add(Operation("B", j, i, k))
                sigma.add(Operation("C", j, i, k))

    return sigma


def add_symmetry(D: set[tuple[Operation, Operation]]):
    to_add = set()
    for elem in D:
        to_add.add((elem[1], elem[0]))
    D.update(to_add)


def add_transitive_closure(D: set[tuple[Operation, Operation]]) -> set[tuple[Operation, Operation]]:
    def dfs(node, reachable):
        nonlocal Graph
        if node not in Graph:
            return
        for neighbour in Graph[node]:
            if neighbour not in reachable:
                reachable.add(neighbour)
                dfs(neighbour, reachable)

    Graph = dict()

    for (a, b) in D:
        Tab = Graph.get(a, [])
        Tab.append(b)
        Graph[a] = Tab

    closure = {node: set() for node in Graph}
    for node in Graph:
        dfs(node, closure[node])

    result = set()
    for node in closure:
        for reachable_node in closure[node]:
            result.add((node, reachable_node))

    return result


def gen_dependency_set(N):
    D = set()
    D1 = set()
    D2 = set()
    D3 = set()
    D4 = set()
    D5 = set()

    I_sigma = set()

    for i in range(N):
        for j in range(i+1, N):
            for k in range(i, N+1):
                D1.add((Operation("A", j, i), Operation("B", j, i, k)))
                D2.add((Operation("B", j, i, k), Operation("C", j, i, k)))

    for i in range(1, N):
        for j in range(i+1, N):
            for a in range(i):
                D3.add((Operation("C", j, a, i), Operation("A", j, i)))
                D3.add((Operation("C", i, a, i), Operation("A", j, i)))

    for i in range(1, N):
        for j in range(i+1, N):
            for k in range(i, N+1):
                for a in range(i):
                    D4.add((Operation("C", j, a, k), Operation("B", j, i, k)))

    for i in range(1, N):
        for j in range(i+1, N):
            for k in range(i, N+1):
                D5.add((Operation("C", j-1, i-1, k), Operation("C", j, i, k)))
                D5.add((Operation("C", j, i-1, k), Operation("C", j, i, k)))

    for elem in gen_sigma(N):
        I_sigma.add((elem, elem))

    D = D1 | D2 | D3 | D4 | D5

    D = add_transitive_closure(D)
    add_symmetry(D)

    return D | I_sigma


def gen_word(N):
    w = []
    for i in range(N-1):
        for j in range(i+1, N):

            w.append(Operation("A", j, i))
            for k in range(i, N+1):
                w.append(Operation("B", j, i, k))
            for k in range(i, N+1):
                w.append(Operation("C", j, i, k))

    return w


def gen_independency_set(Dependency_set: set, Sigma: set):
    # add everything that is not in D
    I = set()
    for elem1 in Sigma:
        for elem2 in Sigma:
            if (elem1, elem2) not in Dependency_set:
                I.add((elem1, elem2))
    return I


def createDependencyGraph(w, D):
    graph = [[] for _ in range(len(w))]
    for i in range(len(w)):
        for j in range(i+1, len(w)):
            if (w[i], w[j]) in D:
                graph[i].append(j)

    return graph


def reduceGraph(graph):
    n = len(graph)

    graph = [set(neighbors) for neighbors in graph]

    for u in range(n):
        to_remove = set()

        for v in graph[u]:
            to_remove.update(graph[u] & graph[v])

        graph[u] -= to_remove

    return [list(neighbors) for neighbors in graph]


def foatNormalForm(graph, w):
    graph2 = deepcopy(graph)
    result = []
    resultNumbers = []
    included = [False] * len(w)
    while False in included:
        fullset = {i for i in range(len(w)) if not included[i]}
        seen = set()
        for node in graph2:
            for v in node:
                seen.add(v)
        resultset = fullset - seen

        currFoat = []
        currArr = []
        for item in resultset:
            currArr.append(item)
            included[item] = True
            currFoat.append(w[item])
            graph2[item] = []

        result.append(currFoat)
        resultNumbers.append(currArr)

    return result, resultNumbers


def drawGraph(graph, w, FNF_helper):
    print(graph)
    G = nx.DiGraph()
    for i in range(len(graph)):
        G.add_node(i)
    for node, neighbors in enumerate(graph):
        for neighbor in neighbors:
            G.add_edge(node, neighbor)

    node_labels = {node: w[node] for node in range(len(w))}

    pos = {}

    for i in range(len(FNF_helper)):
        for j in range(len(FNF_helper[i])):
            pos[FNF_helper[i][j]] = (j, -1*i)

    plt.figure(figsize=(20, 30))

    nx.draw(
        G, pos, with_labels=False, node_color='lightblue', edge_color='gray',
        node_size=1000
    )

    nx.draw_networkx_labels(G, pos, labels=node_labels,
                            font_size=10, font_color='black')
    plt.axis('off')
    plt.show()


def manual_scheduler(Matrix, FNF, should_print):
    if should_print:
        print(f"EXECUTING PARALEL ALGORITHM ON:\n{Matrix}")

    n, m = Matrix.shape

    m_Matrix = torch.zeros_like(Matrix, device=Matrix.device)
    n_Matrix = torch.zeros_like(Matrix, device=Matrix.device)

    for block_id, block in enumerate(FNF):
        if should_print:
            print(f"Executing Block {block_id + 1}: {block}")
        for op in block:

            if op.op_type == "A":
                j, i = op.j, op.i
                m_Matrix[j, i] = Matrix[j, i] / Matrix[i, i]

            elif op.op_type == "B":
                j, i, k = op.j, op.i, op.k
                n_Matrix[j, k] = Matrix[i, k] * m_Matrix[j, i]

            elif op.op_type == "C":
                j, i, k = op.j, op.i, op.k
                Matrix[j, k] -= n_Matrix[j, k]
                if abs(Matrix[j, k]) < 1e-6:
                    Matrix[j, k] = 0

            else:
                raise ValueError(f"K*rwa jak: {op}")

        torch.cuda.synchronize()

    return Matrix


def main_function(N, Matrix, should_print):
    start = time.time()

    Sigma = gen_sigma(N)
    D = gen_dependency_set(N)
    w = gen_word(N)
    G = reduceGraph(createDependencyGraph(w, D))
    FNF, FNF_NUMBERS = foatNormalForm(G, w)

    fnf_time = time.time()
    result = manual_scheduler(Matrix, FNF, should_print)

    # result prints
    if should_print:
        print(f"Σ = {Sigma}")
        print(f"D = {D}\n")
        print(f"w = {w}\n")
        print(f"RESULT FNF:\n{FNF}\n")
        print(f"Graf Diekerta:\n{G}\n")
        # drawGraph(G, w, FNF_NUMBERS)

        print(f"Final Resultant Matrix:\n{result.cpu().numpy()}")

    end = time.time()
    print(
        f"Done :) \nTime to create FNF: {(fnf_time-start):.{4}f}s \nTime to perform paralel algorithm: {(end-fnf_time):.{4}f}s")

    return result.cpu().numpy()


def parse_input(input_file, device):
    first_line = True
    matrix_row = 0
    for line in open(input_file, "r"):
        line.strip()
        if first_line:  # ah yes programer
            N = int(line)
            Matrix = torch.zeros((N, N+1), dtype=torch.float32, device=device)
            first_line = False
            continue
        if matrix_row < N:
            elements = [float(i) for i in line.split()]
            for i, elem in enumerate(elements):
                Matrix[matrix_row, i] = elem
            matrix_row += 1
        else:
            elements = [float(i) for i in line.split()]
            for i, elem in enumerate(elements):
                Matrix[i, N] = elem
            break

    return N, Matrix


def save_output(N, Matrix, output_file):
    with open(output_file, 'w') as f:
        f.write(f"{N}\n")

        for row in Matrix:
            square_matrix_row = " ".join(f"{x: .{8}f}" for x in row[:-1])
            f.write(f"{square_matrix_row}\n")

        additional_vector = " ".join(
            f"{x: .{8}f}" for x in [row[-1] for row in Matrix])
        f.write(f"{additional_vector}\n")


if __name__ == "__main__":

    device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")

    if len(sys.argv) < 3:
        raise Exception("Please provide filename!")
    else:
        input_file, output_file = sys.argv[1:3]

    should_print = False
    if len(sys.argv) > 3:
        should_print = (sys.argv[3] != "0")

    N, Matrix = parse_input(input_file, device)
    Matrix = main_function(N, Matrix, should_print)
    save_output(N, Matrix, output_file)
