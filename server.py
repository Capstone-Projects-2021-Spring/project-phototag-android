from flask import Flask, jsonify, request, session, redirect
# from imageai.Detection import ObjectDetection
import os
import time

app = Flask(__name__)
'''
execution_path = os.getcwd()

detector = ObjectDetection()
detector.setModelTypeAsRetinaNet()
detector.setModelPath( os.path.join(execution_path , "resnet50_coco_best_v2.1.0.h5"))
detector.loadModel()
'''


@app.route('/uploadImage', methods=['POST'])
def upload_image():
    username = request.form.get('username')
    print(f'Receiving image from {username}')

    upload_file = request.files['file']
    print(upload_file.filename)
    '''
    if uploaded_file.filename != '':
        uploaded_file.save(uploaded_file.filename)
    
    detections = detector.detectObjectsFromImage(input_image=os.path.join(execution_path, "test.jpeg"),
                                                 output_image_path=os.path.join(execution_path, "imagenew.jpeg"))

    for eachObject in detections:
        print(eachObject["name"], " : ", eachObject["percentage_probability"])
    '''
    # os.remove(uploaded_file.filename)
    # file.save(os.path.join(app.config['upload_folder'], filename))
    return jsonify({'status': 200, 'labels': 'test, '+username})

@app.route('/getData', methods=['GET'])
def retrieve_images():
    print(request.json)
    content = request.json
    username = content["username"]
    password = content["password"]
    n = 0
    result = {'numImages': n, 'images': {'imageId': [], 'imageId': []}}
    '''
    query = query
    '''
    return jsonify({'status': 200, 'results': result})
if __name__ == '__main__':
    print('Starting server')
    app.run()