import json
import os

import numpy as np
from keras import Sequential
from keras.engine.saving import load_model

from CorpusVectorization import get_model, latest_model
from util import prepare_test_data


def evaluate_all_models():
    with open("../common/data/trained/vectors_test.json", 'r', encoding="utf-8") as f:
        test_data = json.load(f)

    local_model_dir = "models\\keras"

    classes = {0: "negatif", 1: "positif", 2: "mixte", 3: "autre"}

    # Evaluate all models
    for file in os.listdir(local_model_dir):
        file_path = os.path.join(local_model_dir, file)
        output_file = "../common/data/metrics/keras/result_{}.txt".format(file)

        # Chargement des modèles
        if os.path.isfile(file_path):
            if os.path.isfile(output_file):
                print("Test file for model {} already exists, skipping...".format(file))
            else:
                print("Generating test file for model {} ...".format(file))
                model = load_model(file_path)

                output = []

                for data in test_data:
                    if isinstance(model, Sequential):
                        output_class = model.predict_classes(np.array([np.array(data["message"])]))
                        output_class = classes.get(output_class[0])
                        output.append([data["id"], output_class])
                    else:
                        data["message"] = np.expand_dims(np.array(data["message"]), axis=1)
                        data["message"] = np.expand_dims(data["message"], axis=0)
                        output_class = model.predict(data["message"])

                        print(output_class[0])
                        output_class = classes.get(list(output_class[0]).index(max(output_class[0])))
                        output.append([data["id"], output_class])

                with open(output_file, 'w', encoding="utf-8") as f:
                    for result in output:
                        f.write("{} {}\n".format(result[0], result[1]))


def format_test_data():
    test_data = []

    with open("../common/data/raw/test.txt", 'r', encoding="utf-8") as f:
        for line in f:
            split_line = line.split(" ", 1)
            test_data.append({"_id": split_line[0], "message": split_line[1].rstrip()})

    prepare_test_data(test_data)


if __name__ == '__main__':
    # format_test_data()

    evaluate_all_models()
