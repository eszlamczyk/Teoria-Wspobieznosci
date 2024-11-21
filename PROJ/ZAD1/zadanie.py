from copy import deepcopy
import networkx as nx
import matplotlib.pyplot as plt
import sys
import os
import re

'''
Klasa tranzakcji

Celem tej klasy jest przechowywanie informacji dot. danej tranzakcji.

Pole left trzyma symbol na lewo od :=
Pole rightArray trzyma liste wszystkich symboli po prawej od :=

Przykładowo dla wejścia

x := x + y + z * v

Pola będą wyglądać następująco
left = 'x'
right = ['x', 'y', 'z', 'v']
'''


class Transaction:
    def __init__(self, transaction_string: str) -> None:
        # znak := nie powinien występować więcej niż raz, więc jeżeli będzie taka linia to kod i tak rzuci błąd
        # (zakładam poprawne dane w większości przypadków)
        left, right = transaction_string.split(":=")
        self.left = left.strip()  # usuwanie spacji
        self.rightArray = [s.strip() for s in re.split(
            r'[+\-*/0-9]', right) if s.strip()]  # dzielenie prawej wg symbolów + - * / oraz usuwanie niepotrzebnych cyfr


'''
parse_file

funkcja przyjmuje ścieżkę do pliku,
następnie ten plik otwiera i zczytuje po linijce (usuwając białe znaki z krawędzi lewej/prawej)
parser ten jest uproszczony na potrzeby zadania, przewiduje 3 poprawne konfiguracje:

- (symbolTranzakcji) tranzakcje:
    wykrywane poprzez sprawdzenie pierwszego znaku przyciętej linii;
    jest to bardzo nieelastyczne ale zakładam poprawność danych wejściowych na podstawie danych przesłanych na UPELu
    drugi znak po znaku ( to jest symbol tranzakcji który zostaje wrzucony do wynikowego słownika, w którym klucz to symbol
    a wartość to rekord Tranzakcji
- A = {alfabet}
    wykrywanie poprzez sprawdzenie pierwszego znaku przyciętej linii;
    używając wbudowanej biblioteki regular expression (re) znajduje wszystkie elementy znajdujące się w nawiasach wąsatych,
    a następnie je rozdzielam i przycinam aby mieć liste alfabetu
- w = <słowo>
    wykrywanie poprzez sprawdzenie pierwszego znaku przyciętej linii;
    wystarczy prosty split po białych znakach i wybranie ostatniego z nich

Następnie stworzone dane przechodzą pewną obróbke
W razie gdyby była tranzakcja (symbol, tranzakcja) gdzie symbol nie należy do A, powinniśmy ją usunąć

Ostatecznie funkcja zwraca przyciętą tranzakcje i słowo
'''


def parse_file(fileName: str) -> tuple[dict[str, Transaction], str]:
    transactions = {}  # dict
    for line in open(fileName, "r"):
        line = line.strip()  # usuwanie spacji z przodu/tyłu linii
        if line[0] == "(":  # linia z tranzakcją
            transactions[line[1]] = Transaction(line[3:])

        if line[0] == "A":  # linia z alfabetem
            # wyszukiwanie wszystkiego w nawiasach wąsowych
            elements_str = re.search(r'\{(.*?)\}', line)

            # potrzebne checki aby interpreter nie wariował i rzucał błędami na lewo i prawo
            # teoretycznie re.search i .group może zwrócić None
            if elements_str is not None:
                elements_str = elements_str.group(1)

            if elements_str is not None:
                A = [element.strip()
                     for element in elements_str.split(',')]

        if line[0] == "w":  # linia ze słowem
            w = line.split()[-1].strip()

    # przycinanie słownika tranzakcji o tranzakcje nie występujące w alfabecie
    transactions = {key: transactions[key] for key in A if key in transactions}

    return transactions, w


'''
create_dependency_sets

funkcja przyjmuje słownik tranzakcji i zwraca zbiór D i I

funkcja podwójnie iteruje po wszystkich tranzakcjach (Sigma^2) i dodaje krotki do odpowiednich zbiorów:
- jeżeli klucze (symbole tranzakcji z alfabetu) są takie same, dodaje je do D
- jeżeli lewa strona tranzacji jest równa lewej lub prawej stronie drugiej tranzakcji (i na odwrót), także dodaje do D
- w innym przypadku dodaje do I
'''


def create_dependency_sets(tanstransactions: dict[str, Transaction]) -> tuple[set, set]:
    D = set()
    I = set()
    for k1 in tanstransactions:
        for k2 in tanstransactions:
            if k1 == k2:
                D.add((k1, k2))
            if (tanstransactions[k1].left in tanstransactions[k2].rightArray or
                transactions[k1].left == transactions[k2].left or
                    tanstransactions[k2].left in tanstransactions[k1].rightArray):
                D.add((k1, k2))
            else:
                I.add((k1, k2))
    return D, I


'''
create_dependency_graph

funkcja przyjmuje słowo i zbiór D i zwraca graf w postaci listy sąsiedztwa

funkcja iteruje pokolei przez symbole słowa i w wewnętrznej pętli przez symbole występujące po i-tym symbolu
jezeli kombinacja i-tego i j-tego symbolu występuje w D, dodajemy krawędź do grafu
'''


def create_dependency_graph(w, D):
    graph = [[] for _ in range(len(w))]
    for i in range(len(w)):
        for j in range(i+1, len(w)):
            if (w[i], w[j]) in D:
                graph[i].append(j)
    return graph


'''
reduce_graph

funkcja przyjmuje DAG i następnie usuwa wszystkie krawędzie które stanowią dłuższy bok trójkątu.
Do wykonania tego algorytmu został użyty:
- https://stackoverflow.com/questions/1690953/transitive-reduction-algorithm-pseudocode
- Harry Hsu. "An algorithm for finding a minimal equivalent graph of a digraph.", Journal of the ACM, 22(1):11-16, January 1975.
'''


def path_graph(graph):
    n = len(graph)
    p = [set(graph[i]) for i in range(n)]
    for i in range(n):
        for j in range(n):
            if i == j:
                continue
            if j in p[i]:
                for k in range(n):
                    if k != i and k != j and k in p[j]:
                        p[i].add(k)
    return p


def reduce_graph(graph):
    """ Transforms a given directed acyclic graph into its minimal equivalent """
    graph = path_graph(graph)
    n = len(graph)
    for j in range(n):
        for i in range(n):
            if i != j and j in graph[i]:
                for k in range(n):
                    if k != i and k != j and k in graph[j]:
                        if k in graph[i]:
                            graph[i].remove(k)
    return graph


'''
foata_normal_form

funkcja przyjmuje zredukowany graf i słowo w

pierwsze tworzy jego kopie (ponieważ graf przyda się jeszcze do rysowania)
następnie krokowo znajduje wszystkie wieszchołki które nie mają żadnych krawędzi wejściowych
dodaje je do obecnej części FNF, poczym usuwa te wieszchołki aby w kolejnej iteracji znaleśc nowe

funkcja zwraca FNF w postaci znaków alfabetu i w postaci numerów wieszchołków (potrzebne do rysowania)
'''


def foata_normal_form(graph, w: str) -> tuple[list, list]:
    copied_graph = deepcopy(graph)
    result = []
    result_numbers = []
    # tablica sprawdzająca czy dany wieszchołek był już wzięty pod uwage w FNF
    included = [False] * len(w)
    while False in included:
        # tworzenie zbioru elementów które jeszcze nie są w FNF
        potential_elements = {i for i in range(len(w)) if not included[i]}
        seen = set()
        for node in copied_graph:
            for v in node:
                # istnieje krawędź z node do v -> v nie może być w FNF
                seen.add(v)
        # różnica zbiorów resultset = potential_elements \ seen
        resultset = potential_elements - seen

        curr_sequence = ""
        curr_sequence_numbers = []
        for found_index in resultset:
            included[found_index] = True
            # dodanie do FNF
            curr_sequence_numbers.append(found_index)
            curr_sequence += w[found_index]
            # usuwanie z grafu
            copied_graph[found_index] = []

        result.append(curr_sequence)
        result_numbers.append(curr_sequence_numbers)

    return result, result_numbers


'''
FUNKCJE POMOCNICZE

draw_graph

za pomocą matplotlib oraz networx funkcja ta rysuje grafy
używa ona grafu operującego na liczbach jako wieszchołkach
a na koniec nadpisuje labelki aby graf był czytelny

warto zauważyć ustawianie pozycji, które wykorzystuje przed chwilą zwrócone result_numbers 
w celu pogrupowania tego grafu po kolejnych jego niezależnych od siebie części co daje ładny graf

print_FNF

funkcja przyjmuje liste FNF a potem przekształca ją w string podobny do tego z przykładów
'''


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

    print(string)


'''
main

preferowane wywołanie terminalowe: 
python zadanie.py <jedna lub więcej ścieżek względnych do plików tekstowych>

wymagane biblioteki
matplotlib, networkx
'''

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
            FNF, FNF_NUMBERS = foata_normal_form(G, w)
            print_FNF(FNF)
            draw_graph(G, w, FNF_NUMBERS, file_name)
