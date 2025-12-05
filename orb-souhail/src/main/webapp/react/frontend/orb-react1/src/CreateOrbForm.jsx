import { useState } from "react";
import { createOrb } from "./services/orbService";

export default function CreateOrbForm({ refresh }) {
    const [x, setX] = useState(100);
    const [y, setY] = useState(100);
    const [size, setSize] = useState(20);
    const [xSpeed, setXSpeed] = useState(3);
    const [ySpeed, setYSpeed] = useState(3);

    async function handleSubmit(e) {
        e.preventDefault();

        const newOrb = { x, y, size, xSpeed, ySpeed };
        await createOrb(newOrb);
        refresh();
    }

    return (
        <form onSubmit={handleSubmit} style={{ marginBottom: "20px" }}>
            <div>
                X: <input type="number" value={x} onChange={e => setX(+e.target.value)} />
            </div>
            <div>
                Y: <input type="number" value={y} onChange={e => setY(+e.target.value)} />
            </div>
            <div>
                Size: <input type="number" value={size} onChange={e => setSize(+e.target.value)} />
            </div>
            <div>
                X Speed: <input type="number" value={xSpeed} onChange={e => setXSpeed(+e.target.value)} />
            </div>
            <div>
                Y Speed: <input type="number" value={ySpeed} onChange={e => setYSpeed(+e.target.value)} />
            </div>

            <button type="submit">Create Orb</button>
        </form>
    );
}
