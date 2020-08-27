import librosa
import tensorflow as tf
import numpy as np

SAVED_MODEL_PATH = "model.h5"
SAMPLES_TO_CONSIDER = 22050

class _Keyword_Spotting_Service:

    model = None
    _mapping = [
        "down",
        "off",
        "on",
        "no",
        "yes",
        "stop",
        "up",
        "right",
        "left",
        "go"
    ]
    _instance = None


    def predict(self, file_path):
        global MFCCs

        # extract MFCC
        MFCCs = self.preprocess(file_path)

        MFCCs = MFCCs[np.newaxis, ..., np.newaxis]

        # get the predicted label
        predictions = self.model.predict(MFCCs)
        predicted_index = np.argmax(predictions)
        predicted_keyword = self._mapping[predicted_index]
        return predicted_keyword


    def preprocess(self, file_path, num_mfcc=13, n_fft=2048, hop_length=512):

        # load audio file
        signal, sample_rate = librosa.load(file_path)

        if len(signal) >= SAMPLES_TO_CONSIDER:
            # ensure consistency of the length of the signal
            signal = signal[:SAMPLES_TO_CONSIDER]

            # extract MFCCs
            MFCCs = librosa.feature.mfcc(signal, sample_rate, n_mfcc=num_mfcc, n_fft=n_fft,
                                         hop_length=hop_length)
        return MFCCs.T


def Keyword_Spotting_Service():

    # ensure an instance is created only the first time the factory function is called
    if _Keyword_Spotting_Service._instance is None:
        _Keyword_Spotting_Service._instance = _Keyword_Spotting_Service()
        _Keyword_Spotting_Service.model = tf.keras.models.load_model(SAVED_MODEL_PATH)
    return _Keyword_Spotting_Service._instance


def run():
    # create 2 instances of the keyword spotting service
    kss = Keyword_Spotting_Service()

    # check that different instances of the keyword spotting service point back to the same object (singleton)
    keyword1 = kss.predict("dataset/down/0a7c2a8d_nohash_0.wav")
    keyword2 = kss.predict("dataset/up/0d2bcf9d_nohash_0.wav")
    keyword3 = kss.predict("dataset/left/0a7c2a8d_nohash_0.wav")
    # keyword = kss.predict("train/test/right.wav")

    return keyword1, keyword2, keyword3

if __name__ == "__main__":

    x=run()

    # make a prediction
    print(f"Predicted keywords: {x[0]}, {x[1]}, {x[2]}")
    # print(f"Predicted keywords: {keyword}")
