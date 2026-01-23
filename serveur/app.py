from flask import Flask, request, jsonify
import tensorflow as tf
from tensorflow.keras.preprocessing import image
import numpy as np
from PIL import Image
import io

model = tf.keras.models.load_model('models/face_shape_model.h5')

app = Flask(__name__)

def preprocess_image(img_bytes):
    img = Image.open(io.BytesIO(img_bytes))
    img = img.resize((64, 64))  
    img_array = np.array(img) / 255.0 
    img_array = np.expand_dims(img_array, axis=0)  
    return img_array


@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({'error': 'No file part'}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400

    img_bytes = file.read()
    img_array = preprocess_image(img_bytes)
    predictions = model.predict(img_array)
    
   
    predicted_class = np.argmax(predictions, axis=1)[0]
    confidence = np.max(predictions)  

    return jsonify({
        'predicted_class': int(predicted_class),
        'confidence': float(confidence)
    })


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
