import random



def gen_ranking():

    options = ["Authentic goods;",
"Product quality assurance;",
"Convenience;",
"Price;",
"Sales service;",
"Delivery cost;",
"Lead time;"]



    probability = [0.28,
0.2,
0.16,
0.12,
0.12,
0.08,
0.04]

    shuffled = []

    with open("./output.txt","w") as f:
        for _ in range(200):
            i = 0
            copy_probability = probability.copy()
            copy_options = options.copy()
            while len(shuffled) != len(options):
                remaining = sum(copy_probability)
                j = i % len(copy_options)
                if random.random() < copy_probability[j]/remaining:
                    if copy_options[j] not in shuffled:
                        shuffled.append(copy_options.pop(j))
                        copy_probability.pop(j)
                i += 1
            print(shuffled)
            f.write("".join(shuffled) + "\n")
            shuffled = []
    
def gen_options():
    options = ["New product updates;",
"Discounts;",
"Seasonal campaign;",
"Lookbook i.e. photos on how to style products;",
"I am not interested in receiving promotional content;",
"Other;"]


    probability = [0.4,
0.72,
0.56,
0.2,
0.08,
0.04]


    choices = []

    with open("./output.txt","w") as f:
        for _ in range(200):
            for i,v in enumerate(options):
                token = random.random()
                if token < probability[i]:
                    choices.append(v)
                    if i == 4:
                        choices = ["I am not interested in receiving promotional content;"]
                        break
            if len(choices) == 0:
                choices = ["I am not interested in receiving promotional content;"]
            print(choices)
            f.write("".join(choices) + "\n")
            choices = []
    
gen_options()

def trash():
    final = random.random()
    for i,v in enumerate(probability):
        if final < v:
            choices = [options[i]]
            break
        else:
            final -= probability[i]