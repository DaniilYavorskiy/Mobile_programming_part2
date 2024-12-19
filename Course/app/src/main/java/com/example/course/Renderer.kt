package com.example.course

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mVPMatrix = FloatArray(16)
    private val normalMatrix = FloatArray(16)
    private val lightPos = floatArrayOf(0.00f, 0.3f, 0f)
    private val cucumberInGlass = floatArrayOf(0.5f, 0.0f, -0.1f)

    private lateinit var table: Table
    private lateinit var glass: Glass
    private lateinit var apple: Sphere
    private lateinit var pumpkin: Sphere
    private lateinit var orange: Sphere
    private lateinit var cucumber: Sphere

    private var appleTexture: Int = 0
    private var pumpkinTexture: Int = 0
    private var orangeTexture: Int = 0
    private var cucumberTexture: Int = 0

    override fun onSurfaceCreated(arg0: GL10?, arg1: EGLConfig?) {
        GLES20.glClearColor(0f,0f,0f, 0.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LESS)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        table = Table(context)

        apple = Sphere(radius = 0.3f/3)
        appleTexture = loadTexture(R.drawable.apple)

        glass = Glass(context)

        pumpkin = Sphere(radius = 0.9f/3)
        pumpkinTexture = loadTexture(R.drawable.tikva)

        orange = Sphere(radius = 0.35f/3)
        orangeTexture = loadTexture(R.drawable.orange)

        cucumber = Sphere(radius = 0.05f, stretchFactorY = 5f)
        cucumberTexture = loadTexture(R.drawable.cucumber)

    }

    override fun onDrawFrame(arg0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        Matrix.setLookAtM(viewMatrix, 0, 1f, 1f, 2f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Установление единичной матрицы (дефолт позиция, масштаб, поворот)
        Matrix.setIdentityM(modelMatrix, 0)

        // Инверсия, чтобы правильно вычислить нормали
        Matrix.invertM(normalMatrix, 0, viewMatrix, 0)

        // Перемещение вдоль осей
        Matrix.translateM(modelMatrix, 0, 0f, -0.3f, 0f)

        // Поворот
        Matrix.rotateM(modelMatrix, 0, 0f, 0.1f, 0.1f, 0f)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Итоговая матрица для рендеринга
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
        Matrix.setIdentityM(modelMatrix, 0)
        table.draw(mVPMatrix, normalMatrix, lightPos, viewMatrix)

        // Тыква
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.translateM(modelMatrix, 0, -0.0f, 0.1f, -0.5f)
        Matrix.rotateM(modelMatrix, 0, 180f, 0f, 1f, 0f)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
        pumpkin.draw(mVPMatrix, normalMatrix, lightPos, viewMatrix, pumpkinTexture)

        // Яблоко
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0.75f, 0.0f, 0.6f)
        Matrix.rotateM(modelMatrix, 0, 180f, 0f, 1f, 0f)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
        apple.draw(mVPMatrix, normalMatrix, lightPos, viewMatrix, appleTexture)

        // Апельсин
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.translateM(modelMatrix, 0, -0.3f, 0.0f, 0.9f)
        Matrix.rotateM(modelMatrix, 0, 180f, 0f, 1f, 0f)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
        orange.draw(mVPMatrix, normalMatrix, lightPos, viewMatrix, orangeTexture)

        // Огурец
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, -0.0f, 0f, 0.2f)
        Matrix.rotateM(modelMatrix, 0, 90f, 3f, 1f, 5f) // Поворот вокруг оси Y
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
        cucumber.draw(mVPMatrix, normalMatrix, lightPos, viewMatrix, cucumberTexture)

        // Стакан
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.translateM(modelMatrix,0, cucumberInGlass[0],
            cucumberInGlass[1],
            cucumberInGlass[2]
        )
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
        glass.draw(mVPMatrix, lightPos, viewMatrix)
    }

    override fun onSurfaceChanged(arg0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 20f) // увеличен диапазон дальности

        Matrix.setLookAtM(viewMatrix, 0, 1f, 1f, 2f, 0f, 0f, 0f, 0f, 1f, 0f)

    }


    private fun loadTexture(resourceId: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()

        return textureId
    }
}