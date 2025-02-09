****Parts Manager****

A mobile application that allows users to efficiently track and manage spare parts for various mobile phones. 
Users can easily add, delete, and update parts directly in the database, ensuring streamlined inventory management.

**Features**
  - User Authentication: Login and registration functionality for secure access.
  - Add New Parts: Create a new part with an image, name, model, and quantity.
  - Search Functionality: Quickly search for specific parts using the integrated search bar.
  - Database Management: Add or remove parts from the Firebase database in real time.

**Technologies Used**
  Java: Core language for application development.
  Firebase: Used for online data storage and real-time database management.
  Picasso: Library for loading and displaying images efficiently within the app.

**Installation and Setup**
  1. Clone the repository:
      git clone https://github.com/luciastojic/nramu1.git
  2. Open in Android Studio:
    - Connect your mobile device to your laptop using a USB cable.
    - Open Android Studio, and ensure your mobile device is detected.
    - Click on Run to build and deploy the app on your device.
  4. irebase Setup:
    - Ensure you have a Firebase project set up.
    - Add your google-services.json file to the app directory in Android Studio.

**Usage**
  1. Login/Register: Create a new account or login to an existing one.
  2. Adding a New Part:
    - Tap the "+" button.
    - Fill in details like image, name, model, and quantity.
    - Save to add it directly to Firebase.
  3. Managing Parts:
    - Use the search bar to find specific parts.
    - Select a part to update its quantity or delete it.

**Screenshots**

![Screenshot_20250210_105541 (1)](https://github.com/user-attachments/assets/b9b0935d-a511-444e-9463-982903636adc)
![Screenshot_20250210_110901](https://github.com/user-attachments/assets/77a80006-f85e-49ea-93a0-697e8284a204)
![WhatsApp Image 2025-02-10 at 10 39 24 (3)](https://github.com/user-attachments/assets/e9cae501-43de-4b21-a439-a45c7aab3d91)
![Screenshot_20250210_110933](https://github.com/user-attachments/assets/7460735f-5d83-4f2e-b49b-97e88ae1ec64)

****Key Code Snippets****

**Adding a New Part to Firebase:**

  DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");
  String itemId = databaseReference.push().getKey();
  databaseReference.child(itemId).setValue(newPart);
  
**Loading Images Using Picasso:**

  Picasso.get().load(imageUrl).into(imageView);
  
**Login:**

  mAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    });



