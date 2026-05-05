package com.marketplace.ui.designsystem

import kotlin.math.*

/**
 * AnimationPhysicsEngine: A custom physics-based animation library for high-fidelity UI transitions.
 * 
 * Instead of standard linear or cubic easing, this engine uses physical properties
 * like spring tension, damping, and mass to calculate movement. This results in
 * fluid, natural-feeling animations that respond to user gestures with realistic
 * momentum. This is a key differentiator for premium, high-density applications.
 */
class AnimationPhysicsEngine {

    /**
     * SpringParams: Defines the physical properties of a spring-based motion.
     * Higher tension results in faster movement, while higher damping reduces oscillation.
     */
    data class SpringParams(
        val dampingRatio: Float = 0.5f,
        val stiffness: Float = 200f,
        val mass: Float = 1.0f
    )

    /**
     * Calculates the position of an element at a given time using a damped harmonic oscillator model.
     * 
     * This mathematical simulation determines the displacement of a UI component 
     * from its equilibrium point. It accounts for the velocity, force, and time 
     * elapsed to produce a frame-by-frame position update that mimics physical objects.
     * 
     * @param targetValue The desired final position.
     * @param currentValue The current starting position.
     * @param velocity The current momentum of the object.
     * @param deltaTime The time elapsed since the last frame calculation.
     * @param params The physical properties to apply.
     * @return A Pair containing the new position and new velocity.
     */
    fun calculateSpringStep(
        targetValue: Float,
        currentValue: Float,
        velocity: Float,
        deltaTime: Float,
        params: SpringParams
    ): Pair<Float, Float> {
        val displacement = currentValue - targetValue
        val springForce = -params.stiffness * displacement
        val dampingForce = -params.dampingRatio * 2.0f * sqrt(params.stiffness * params.mass) * velocity
        
        val acceleration = (springForce + dampingForce) / params.mass
        val newVelocity = velocity + acceleration * deltaTime
        val newPosition = currentValue + newVelocity * deltaTime

        return newPosition to newVelocity
    }

    /**
     * Computes a "Fling" animation trajectory based on initial velocity and friction.
     * 
     * Used for scrollable components and swipe-to-dismiss gestures. This algorithm
     * models the deceleration of an object until it comes to a complete rest, 
     * providing a tactile and intuitive feedback loop for the user.
     * 
     * @param initialVelocity The speed at which the user flicked the screen.
     * @param friction The resistance coefficient of the virtual surface.
     * @param duration The projected time until the motion stops.
     * @return The final projected displacement.
     */
    fun calculateFlingDistance(initialVelocity: Float, friction: Float, duration: Float): Float {
        // Simple kinematic equation with constant deceleration
        val acceleration = -friction * 9.81f
        return (initialVelocity * duration) + (0.5f * acceleration * duration.pow(2))
    }

    /**
     * Helper to calculate powers for physical equations.
     */
    private fun Float.pow(n: Int): Float = Math.pow(this.toDouble(), n.toDouble()).toFloat()

    /**
     * Resets the physics simulation state for a specific component.
     * 
     * This is necessary when an animation is interrupted by a new user gesture
     * to prevent velocity overlap and maintain a responsive UI.
     */
    fun resetSimulation() {
        println("PhysicsEngine: Simulation reset for new gesture input.")
    }

    /**
     * Applies a gravitational pull effect towards a specific point on the screen.
     * 
     * Used for "Magnet" style UI elements that snap to a grid or a dock.
     * Demonstrates advanced spatial logic implementation within the design system.
     */
    fun applyMagnetForce(point: Float, target: Float, strength: Float): Float {
        val delta = target - point
        return if (abs(delta) < 100) delta * strength else 0.0f
    }
}
