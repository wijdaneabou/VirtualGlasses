FROM python:3.10-slim

# Installer quelques librairies système nécessaires pour TensorFlow
RUN apt-get update && apt-get install -y --no-install-recommends \
    libglib2.0-0 libsm6 libxext6 libxrender-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copier le fichier requirements.txt et installer les dépendances
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copier votre code
COPY . .

EXPOSE 5000

# Lancer l'application Flask
CMD ["python", "app.py"]