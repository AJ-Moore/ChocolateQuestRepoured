package mcalibrary.math;

import java.lang.Math;

/**
 * A four-element axis angle represented by single-precision floating point 
 * x,y,z,angle components. An axis angle is a rotation of angle (radians)
 * about the vector (x,y,z).
 *
 */
public class AxisAngle4f implements java.io.Serializable, Cloneable {

	static final long serialVersionUID = -163246355858070601L;

	/**
	 * The x coordinate.
	 */
	public float x;

	/**
	 * The y coordinate.
	 */
	public float y;

	/**
	 * The z coordinate.
	 */
	public float z;

	/**
	 * The angle of rotation in radians.
	 */
	public float angle;

	final static double EPS = 0.000001;

	/**
	 * Constructs and initializes a AxisAngle4f from the specified xyzw coordinates.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param angle  the angle of rotation in radians
	 */
	public AxisAngle4f(float x, float y, float z, float angle)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
	}


	/**
	 * Constructs and initializes an AxisAngle4f from the array of length 4. 
	 * @param a the array of length 4 containing x,y,z,angle in order
	 */
	public AxisAngle4f(float[] a)
	{
		this.x = a[0];
		this.y = a[1];
		this.z = a[2];
		this.angle = a[3];
	}


	/**
	 * Constructs and initializes an AxisAngle4f from the specified 
	 * AxisAngle4f.
	 * @param a1 the AxisAngle4f containing the initialization x y z angle data
	 */
	public AxisAngle4f(AxisAngle4f a1)
	{
		this.x = a1.x;
		this.y = a1.y;
		this.z = a1.z;
		this.angle = a1.angle;
	}


	/**
	 * Constructs and initializes an AxisAngle4f from the specified 
	 * axis and angle.
	 * @param axis the axis
	 * @param angle the angle of rotation in radians
	 */
	public AxisAngle4f(Vector3f axis, float angle) {
		this.x = axis.x;
		this.y = axis.y;
		this.z = axis.z;
		this.angle = angle;
	}


	/**
	 * Constructs and initializes an AxisAngle4f to (0,0,1,0).
	 */
	public AxisAngle4f()
	{
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 1.0f;
		this.angle = 0.0f;
	}


	/**
	 * Sets the value of this axis-angle to the specified x,y,z,angle. 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param angle  the angle of rotation in radians
	 */
	public final void set(float x, float y, float z, float angle)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
	}


	/**
	 * Sets the value of this axis-angle to the specified values in the
	 * array of length 4.
	 * @param a the array of length 4 containing x,y,z,angle in order
	 */
	public final void set(float[] a)
	{
		this.x = a[0];
		this.y = a[1];
		this.z = a[2];
		this.angle = a[3];
	}


	/**
	 * Sets the value of this axis-angle to the value of axis-angle a1.
	 * @param a1 the axis-angle to be copied
	 */
	public final void set(AxisAngle4f a1)
	{
		this.x = a1.x;
		this.y = a1.y;
		this.z = a1.z;
		this.angle = a1.angle;
	}


	/**
	 * Sets the value of this AxisAngle4f to the specified 
	 * axis and angle.
	 * @param axis the axis
	 * @param angle the angle of rotation in radians
	 *
	 * @since vecmath 1.2
	 */
	public final void set(Vector3f axis, float angle) {
		this.x = axis.x;
		this.y = axis.y;
		this.z = axis.z;
		this.angle = angle;
	}


	/**
	 * Copies the value of this axis-angle into the array a.
	 * @param a the array 
	 */
	public final void get(float[] a)
	{
		a[0] = this.x;
		a[1] = this.y;
		a[2] = this.z;
		a[3] = this.angle;
	} 


	/**
	 * Sets the value of this axis-angle to the rotational equivalent
	 * of the passed quaternion.
	 * If the specified quaternion has no rotational component, the value
	 * of this AxisAngle4f is set to an angle of 0 about an axis of (0,1,0).
	 * @param q1  the Quat4f
	 */
	public final void set(Quaternion q1)
	{
		double mag = q1.x*q1.x + q1.y*q1.y + q1.z*q1.z;  

		if ( mag > EPS ) {
			mag = Math.sqrt(mag);
			double invMag = 1.0/mag;

			x = (float)(q1.x*invMag);
			y = (float)(q1.y*invMag);
			z = (float)(q1.z*invMag);
			angle = (float)(2.0*Math.atan2(mag, q1.w)); 
		} else {
			x = 0.0f;
			y = 1.0f;
			z = 0.0f;
			angle = 0.0f;
		}
	}


	/**
	 * Sets the value of this axis-angle to the rotational component of
	 * the passed matrix.
	 * If the specified matrix has no rotational component, the value
	 * of this AxisAngle4f is set to an angle of 0 about an axis of (0,1,0).
	 * @param m1 the matrix4f
	 */
	public final void set(Matrix4f m1)
	{
		Matrix3f m3f = new Matrix3f();

		m1.get(m3f);

		x = m3f.m21 - m3f.m12;
		y = m3f.m02 - m3f.m20;
		z = m3f.m10 - m3f.m01;
		double mag = x*x + y*y + z*z;

		if (mag > EPS) {
			mag = Math.sqrt(mag);
			double sin = 0.5*mag;
			double cos = 0.5*(m3f.m00 + m3f.m11 + m3f.m22 - 1.0);

			angle = (float)Math.atan2(sin, cos);
			double invMag = 1.0/mag;
			x = (float)(x*invMag);
			y = (float)(y*invMag);
			z = (float)(z*invMag);
		} else {
			x = 0.0f;
			y = 1.0f;
			z = 0.0f;
			angle = 0.0f;
		}


	}

	/**
	 * Sets the value of this axis-angle to the rotational component of
	 * the passed matrix.
	 * If the specified matrix has no rotational component, the value
	 * of this AxisAngle4f is set to an angle of 0 about an axis of (0,1,0).
	 * @param m1 the matrix3f
	 */
	public final void set(Matrix3f m1)
	{
		x = (float)(m1.m21 - m1.m12);
		y = (float)(m1.m02 - m1.m20);
		z = (float)(m1.m10 - m1.m01);
		double mag = x*x + y*y + z*z;
		if (mag > EPS) {
			mag = Math.sqrt(mag);
			double sin = 0.5*mag;
			double cos = 0.5*(m1.m00 + m1.m11 + m1.m22 - 1.0);

			angle = (float)Math.atan2(sin, cos);

			double invMag = 1.0/mag;
			x = (float)(x*invMag);
			y = (float)(y*invMag);
			z = (float)(z*invMag);
		} else {
			x = 0.0f;
			y = 1.0f;
			z = 0.0f;
			angle = 0.0f;	    
		}

	}


	/**
	 * Sets the value of this axis-angle to the rotational component of
	 * the passed matrix.
	 * If the specified matrix has no rotational component, the value
	 * of this AxisAngle4f is set to an angle of 0 about an axis of (0,1,0).
	 * @param m1 the matrix3d
	 */
	public final void set(Matrix3d m1)
	{

		x = (float)(m1.m21 - m1.m12);
		y = (float)(m1.m02 - m1.m20);
		z = (float)(m1.m10 - m1.m01);
		double mag = x*x + y*y + z*z;

		if (mag > EPS) {
			mag = Math.sqrt(mag);
			double sin = 0.5*mag;
			double cos = 0.5*(m1.m00 + m1.m11 + m1.m22 - 1.0);

			angle = (float)Math.atan2(sin, cos);

			double invMag = 1.0/mag;
			x = (float)(x*invMag);
			y = (float)(y*invMag);
			z = (float)(z*invMag);
		} else {
			x = 0.0f;
			y = 1.0f;
			z = 0.0f;
			angle = 0.0f;	    
		}
	}


	/**
	 * Returns a string that contains the values of this AxisAngle4f.
	 * The form is (x,y,z,angle).
	 * @return the String representation
	 */  
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
	}


	/**
	 * Returns true if all of the data members of AxisAngle4f a1 are
	 * equal to the corresponding data members in this AxisAngle4f.
	 * @param a1  the axis-angle with which the comparison is made
	 * @return  true or false
	 */  
	public boolean equals(AxisAngle4f a1)
	{
		try {
			return(this.x == a1.x && this.y == a1.y && this.z == a1.z
					&& this.angle == a1.angle);
		}
		catch (NullPointerException e2) {return false;}

	}

	/**
	 * Returns true if the Object o1 is of type AxisAngle4f and all of the
	 * data members of o1 are equal to the corresponding data members in
	 * this AxisAngle4f.
	 * @param o1  the object with which the comparison is made
	 * @return  true or false
	 */  
	public boolean equals(Object o1)
	{
		try {
			AxisAngle4f a2 = (AxisAngle4f) o1;
			return(this.x == a2.x && this.y == a2.y && this.z == a2.z
					&& this.angle == a2.angle);
		}
		catch (NullPointerException e2) {return false;}
		catch (ClassCastException   e1) {return false;}

	}

	/**
	 * Returns true if the L-infinite distance between this axis-angle
	 * and axis-angle a1 is less than or equal to the epsilon parameter, 
	 * otherwise returns false.  The L-infinite
	 * distance is equal to 
	 * MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2), abs(angle1-angle2)].
	 * @param a1  the axis-angle to be compared to this axis-angle 
	 * @param epsilon  the threshold value  
	 */
	public boolean epsilonEquals(AxisAngle4f a1, float epsilon)
	{
		float diff;

		diff = x - a1.x;
		if((diff<0?-diff:diff) > epsilon) return false;

		diff = y - a1.y;
		if((diff<0?-diff:diff) > epsilon) return false;

		diff = z - a1.z;
		if((diff<0?-diff:diff) > epsilon) return false;

		diff = angle - a1.angle;
		if((diff<0?-diff:diff) > epsilon) return false;

		return true;

	}

	/**
	 * Creates a new object of the same class as this object.
	 *
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError if there is not enough memory.
	 * @see java.lang.Cloneable
	 */
	public Object clone() {
		// Since there are no arrays we can just use Object.clone()
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}


	/**
	 * Get the axis angle, in radians.
	 * An axis angle is a rotation angle about the vector (x,y,z). 
	 * 
	 * @return Returns the angle, in radians.
	 */
	public final float getAngle() {
		return angle;
	}


	/**
	 * Set the axis angle, in radians.
	 * An axis angle is a rotation angle about the vector (x,y,z). 
	 * 
	 * @param angle The angle to set, in radians.
	 */
	public final void setAngle(float angle) {
		this.angle = angle;
	}


	/**
	 * Get value of <i>x</i> coordinate.  
	 * 
	 * @return the <i>x</i> coordinate.
	 */
	public final float getX() {
		return x;
	}


	/**
	 * Set a new value for <i>x</i> coordinate.
	 * 
	 * @param x the <i>x</i> coordinate.
	 */
	public final void setX(float x) {
		this.x = x;
	}


	/**
	 * Get value of <i>y</i> coordinate.
	 * 
	 * @return the <i>y</i> coordinate 
	 */
	public final float getY() {
		return y;
	}


	/**
	 * Set a new value for <i>y</i> coordinate.
	 * 
	 * @param y the <i>y</i> coordinate.
	 */
	public final void setY(float y) {
		this.y = y;
	}


	/**
	 * Get  value of <i>z</i> coordinate.
	 * 
	 * @return the <i>z</i> coordinate.
	 */
	public final float getZ() {
		return z;
	}


	/**
	 * Set a new value for <i>z</i> coordinate.
	 * 
	 * @param z the <i>z</i> coordinate.
	 */
	public final void setZ(float z) {
		this.z = z;
	}

}
