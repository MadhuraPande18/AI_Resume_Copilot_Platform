# 🚀 CLOUD DEPLOYMENT GUIDE — HOW TO GO PUBLIC FOR FREE IN 5 MINUTES

This simple guide will take your backend server off your laptop and deploy it permanently to a **free, public cloud server**. Recruits, teachers, and friends can access your site at any time, even when your laptop is closed!

---

## 🛠️ PRE-REQUISITES (Already Completed for You!):
1. Your frontend is deployed on **Vercel**.
2. Your database is hosted on **MongoDB Atlas** in the cloud.
3. Your backend contains a production-grade **`Dockerfile`** and **`application-prod.properties`** which I have already created and pushed to your GitHub!

---

## 🌐 METHOD A: DEPLOYING TO RENDER (100% Free & Highly Recommended)
Render is an extremely popular and user-friendly hosting platform that builds Docker containers out of the box.

### Step 1: Create a Render Account
1. Go to 👉 **[https://render.com](https://render.com)** and sign up using your **GitHub account**.

### Step 2: Create a New Web Service
1. Click the **"New +"** button in your Render dashboard and select **"Web Service"**.
2. Connect your GitHub repository: `MadhuraPande18/AI_Resume_Copilot_Platform`.
3. In the creation form, configure exactly these settings:
   * **Name:** `interview-copilot-backend`
   * **Region:** Select the closest location (e.g., Singapore or Oregon).
   * **Root Directory:** `backend` *(⚠️ Critical: Type `backend` so Render compiles the Java backend directory!)*
   * **Language/Runtime:** Select **`Docker`** *(⚠️ Critical: Render will automatically find the Dockerfile I created and compile your jar!)*
   * **Instance Type:** Select **"Free"** ($0/month).

### Step 3: Add Cloud Environment Variables
Before clicking deploy, scroll down and click **"Advanced"** to add your Environment Variables. Add these three keys:
1. **`SPRING_PROFILES_ACTIVE`** = `prod` *(Loads our cloud application-prod.properties)*
2. **`SPRING_DATA_MONGODB_URI`** = `mongodb+srv://...your_mongodb_atlas_connection_string...` *(Your cloud MongoDB URL)*
3. **`JWT_SECRET`** = `a_random_very_long_secure_password_string_here` *(Secure credentials secret)*

### Step 4: Click Deploy!
Render will automatically pull the code, download Java dependencies, build the executable JAR, and start your live public server! This takes about 2–3 minutes. 

Once finished, you will see a public HTTPS link in the top-left of your Render dashboard:
👉 **`https://interview-copilot-backend.onrender.com`** *(Example link)*

---

## 🔗 STEP 5: CONNECT YOUR VERCEL FRONTEND TO YOUR NEW PUBLIC BACKEND
Once your backend is live on Render:
1. Open the file **`frontend/src/api.js`** in your VS Code (or edit it directly on GitHub).
2. Change the hardcoded API base URL from the local ngrok tunnel to your new public Render link:
   ```javascript
   // Change this:
   const API_BASE_URL = "https://chowtime-upbeat-mandolin.ngrok-free.dev/api";

   // To this:
   const API_BASE_URL = "https://interview-copilot-backend.onrender.com/api";
   ```
3. Save, commit, and push the change to GitHub! 

Vercel will instantly detect the commit and redeploy your frontend in **15 seconds**! 

**And that's it! Your entire full-stack application is now permanently public, 100% free, and completely automated!** 🥳🎉🚀
