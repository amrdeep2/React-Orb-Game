import { useEffect, useState } from "react";
import { getOrbs, deleteOrb } from "./services/orbService";

import CreateOrbForm from "./CreateOrbForm";
import OrbCanvas from "./OrbCanvas";

function App() {
    const [orbs, setOrbs] = useState([]);

    // Load orbs from backend
    async function load() {
        try {
            const data = await getOrbs();
            setOrbs(data);
        } catch (err) {
            console.error("Failed to load orbs:", err);
        }
    }

    useEffect(() => {
        load();
        const interval = setInterval(load, 800); // LIVE UPDATE
        return () => clearInterval(interval);
    }, []);

    // Delete orb
    async function handleDelete(id) {
        await deleteOrb(id);
        load();
    }

    return (
        <div style={{ padding: "20px" }}>
            <h1>React Orbs (LIVE)</h1>

            {/* CREATE ORB FORM */}
            <h2>Create New Orb</h2>
            <CreateOrbForm refresh={load} />

            <hr />

            {/* CANVAS */}
            <h2>Animation</h2>
            <OrbCanvas orbs={orbs} />

            <hr />

            {/* ORB LIST */}
            <h2>Orb List</h2>
            {orbs.map((o) => (
                <div key={o.id}>
                    <strong>ID {o.id}</strong> → X:{o.x} Y:{o.y} Size:{o.size}
                    <button
                        onClick={() => handleDelete(o.id)}
                        style={{ marginLeft: "10px", color: "red" }}
                    >
                        Delete
                    </button>
                </div>
            ))}
        </div>
    );
}

export default App;
