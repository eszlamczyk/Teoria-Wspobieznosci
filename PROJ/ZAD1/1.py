from copy import deepcopy
import networkx as nx
import matplotlib.pyplot as plt
import sys
import os
import re


class Transaction:
    def __init__(self, transaction_string: str) -> None:
        left, right = transaction_string.split(":=")
        self.left = left.strip()
        self.rightArray = [s.strip() for s in re.split(
            r'[+\-*/0-9]', right) if s.strip()]


def parse_file(fileName: str) -> tuple[dict[str, Transaction], str]:
    transactions = {}
    for line in open(fileName, "r"):
        line = line.strip()
        if line[0] == "(":
            transactions[line[1]] = Transaction(line[3:])

        elif line[0] == "A":
            elements_str = re.search(r'\{(.*?)\}', line)
            if elements_str is not None:
                elements_str = elements_str.group(1)

            if elements_str is not None:
                A = [element.strip()
                     for element in elements_str.split(',')]

        else:
            w = line.split()[-1].strip()

    transactions = {key: transactions[key] for key in A if key in transactions}

    return transactions, w


def create_dependency_sets(tanstransactions: dict[str, Transaction]) -> tuple[set, set]:
    D = set()
    I = set()
    for k1 in tanstransactions:
        for k2 in tanstransactions:
            if k1 == k2:
                D.add((k1, k2))
            if tanstransactions[k1].left in tanstransactions[k2].rightArray or tanstransactions[k2].left in tanstransactions[k1].rightArray:
                D.add((k1, k2))
            else:
                I.add((k1, k2))
    return D, I


def create_dependency_graph(w, D):
    graph = [[] for _ in range(len(w))]
    for i in range(len(w)):
        for j in range(i+1, len(w)):
            if (w[i], w[j]) in D:
                graph[i].append(j)
    return graph


def reduce_graph(graph):
    for u in graph:
        for v in u:
            for w in graph[v]:
                if w in u:
                    u.remove(w)

    return graph


def foata_normal_form(graph, w: str) -> tuple[list, list]:
    copied_graph = deepcopy(graph)
    result = []
    result_numbers = []
    included = [False] * len(w)
    while False in included:
        potential_elements = {i for i in range(len(w)) if not included[i]}
        seen = set()
        for node in copied_graph:
            for v in node:
                seen.add(v)
        resultset = potential_elements - seen

        curr_sequence = ""
        curr_sequence_numbers = []
        for found_index in resultset:
            curr_sequence_numbers.append(found_index)
            included[found_index] = True
            curr_sequence += w[found_index]
            copied_graph[found_index] = []

        result.append(curr_sequence)
        result_numbers.append(curr_sequence_numbers)

    return result, result_numbers


def draw_graph(graph, w, FNF_helper, file_name):
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

    plt.figure(figsize=(4, 6))

    nx.draw(
        G, pos, with_labels=False, node_color='lightblue', edge_color='gray',
        node_size=1000
    )

    nx.draw_networkx_labels(G, pos, labels=node_labels,
                            font_size=15, font_color='black')
    plt.axis('off')
    plt.title(f"Minimal dependency graph for {file_name}")
    plt.show()


def print_FNF(FNF):
    string = "FNF([w]) = "
    for combination in FNF:
        string += f"({combination})"


if __name__ == "__main__":

    if len(sys.argv) < 2:
        raise Exception("Please provide filename!")
    else:
        files_to_parse = sys.argv[1:]
        for file_name in files_to_parse:
            print(f"\n\n\n PARSING {file_name}")
            transactions, w = parse_file(os.path.join(os.getcwd(), file_name))
            D, I = create_dependency_sets(transactions)
            print(f"D = {sorted(D, key=lambda x: (x[0], x[1]))}")
            print(f"I = {sorted(I, key=lambda x: (x[0], x[1]))}")
            G = reduce_graph(create_dependency_graph(w, D))
            FNF, FNF_NUMBERS = foata_normal_form(foata_normal_form, w)
            print_FNF(FNF)
            draw_graph(G, w, FNF_NUMBERS, file_name)
