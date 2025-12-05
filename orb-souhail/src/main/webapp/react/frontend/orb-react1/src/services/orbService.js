// src/services/orbService.js

const API_URL = "http://localhost:8080/orb-souhail/resources/orbs";

// BASIC AUTH USER (APIGroup)
const username = "apitest";
const password = "root";
const authHeader = "Basic " + btoa(username + ":" + password);

// GET ALL ORBS
export async function getOrbs() {
    const res = await fetch(API_URL, {
        method: "GET",
        headers: { "Authorization": authHeader }
    });

    if (!res.ok) throw new Error("Failed to fetch orbs");
    return await res.json();
}

// DELETE ORB
export async function deleteOrb(id) {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
        headers: { "Authorization": authHeader }
    });

    if (!res.ok && res.status !== 204)
        throw new Error("Failed to delete orb");
}

// CREATE ORB
export async function createOrb(orb) {
    const payload = {
        x: Number(orb.x),
        y: Number(orb.y),
        size: Number(orb.size),
         XSpeed: Number(orb.xSpeed),
         YSpeed: Number(orb.ySpeed)
    };

    console.log("Sending payload:", payload);

    const res = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Authorization": authHeader,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    if (!res.ok) throw new Error("Failed to create orb");
    return await res.json();
}
