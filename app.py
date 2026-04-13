from flask import Flask, request, jsonify, render_template
import torch
import torch.nn as nn
from transformers import DistilBertTokenizer, DistilBertForSequenceClassification
from torchvision import models, transforms
from PIL import Image
import os


app = Flask(__name__, template_folder='.', static_folder='.')

# =========================
# LOAD TEXT MODEL
# =========================
try:
    text_model = DistilBertForSequenceClassification.from_pretrained("models/text_model")
    tokenizer = DistilBertTokenizer.from_pretrained("models/text_model")
    text_model.eval()
    print("✅ Text Model Loaded")
except Exception as e:
    print(f"❌ Text Model Load Error: {e}")

# =========================
# LOAD IMAGE MODEL (MobileNetV2)
# =========================
try:
    image_model = models.mobilenet_v2(pretrained=False)
    image_model.classifier[1] = nn.Linear(image_model.last_channel, 2)
    image_model.load_state_dict(torch.load("models/image_model.pth", map_location=torch.device('cpu')))
    image_model.eval()
    print("Image Model Loaded")
except Exception as e:
    print(f"Image Model Load Error: {e}")

# =========================
# IMAGE TRANSFORM
# =========================
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
])

# =========================
# PREDICTION FUNCTIONS
# =========================
def predict_text(text):
    if not text: return "HAM", 0.0
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True)
    with torch.no_grad():
        outputs = text_model(**inputs)
        probs = torch.nn.functional.softmax(outputs.logits, dim=-1)
    spam_prob = probs[0][1].item()
    return "SPAM" if spam_prob > 0.3 else "HAM", spam_prob

def predict_image(image_path):
    try:
        image = Image.open(image_path).convert("RGB")
        image = transform(image).unsqueeze(0)
        with torch.no_grad():
            outputs = image_model(image)
            probs = torch.nn.functional.softmax(outputs, dim=1)
            _, pred = torch.max(outputs, 1)
        class_names = ['HAM', 'SPAM']
        return class_names[pred.item()], probs[0][1].item()
    except:
        return "N/A", 0.0

# =========================
# ROUTES
# =========================
@app.route("/")
def home(): return render_template("index.html")

@app.route("/analyze")
def analyze(): return render_template("analyze.html")

@app.route("/dashboard")
def dashboard(): return render_template("dashboard.html")

@app.route("/about")
def about(): return render_template("about.html")

# =========================
# API FOR JAVA SERVLET
# =========================
@app.route("/predict", methods=["POST"])
def predict():
    print("\n--- New Request Received ---")
    text = request.form.get("text", "")
    file = request.files.get("image")
    
    print(f"Text Input: {text[:50]}...")
    
    file_path = None
    if file:
        os.makedirs("uploads", exist_ok=True)
        file_path = os.path.join("uploads", file.filename)
        file.save(file_path)
        print(f"Image Received: {file.filename}")

    text_result, text_prob = predict_text(text)
    
    if file_path:
        image_result, image_prob = predict_image(file_path)
    else:
        image_result, image_prob = "N/A", 0.0

    # Decision Logic
    if text_result == "SPAM" and image_result == "SPAM":
        final = "SPAM"
    elif text_result == "SPAM" or image_result == "SPAM":
        final = "SUSPICIOUS"
    else:
        final = "HAM"

    result = {
        "text_prediction": text_result,
        "text_confidence": round(text_prob, 4),
        "image_prediction": image_result,
        "image_confidence": round(image_prob, 4),
        "final_decision": final
    }
    
    print(f"Final Decision: {final}")
    return jsonify(result)

if __name__ == "__main__":
    app.run(debug=True, port=5001)
