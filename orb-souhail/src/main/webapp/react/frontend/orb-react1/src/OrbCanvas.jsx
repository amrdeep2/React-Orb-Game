import { useEffect, useRef } from "react";

export default function OrbCanvas({ orbs }) {
    const canvasRef = useRef(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext("2d");

        ctx.clearRect(0, 0, canvas.width, canvas.height);

        orbs.forEach(o => {
            ctx.beginPath();
            ctx.arc(o.x, o.y, o.size, 0, 2 * Math.PI);
            ctx.fillStyle = "red";
            ctx.fill();
        });

    }, [orbs]);

    return (
        <canvas
            ref={canvasRef}
            width={800}
            height={600}
            style={{ border: "1px solid black" }}
        />
    );
}
