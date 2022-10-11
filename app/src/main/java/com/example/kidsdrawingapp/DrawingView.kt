package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.FocusFinder
import android.view.MotionEvent
import android.view.View

class DrawingView(context : Context, attrs : AttributeSet) : View(context,attrs) {

    private var mDrawpath : CustomPath?=null
    private var mCanvasBitmap: Bitmap?= null
    private var mDrawPaint : Paint?= null
    private var mCanvasPaint : Paint ?= null
    private var mBrushSize : Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas : Canvas ?= null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPath = ArrayList<CustomPath>()

    init{
        setUpDrawing()
    }

    fun onclickundo(){
        if (mPaths.size > 0){
            mUndoPath.add(mPaths.removeAt(mPaths.size - 1))
            invalidate()
        }
    }

    fun onclickclear(){
        if (mPaths.size > 0){
            mPaths.clear()
            invalidate()
        }
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawpath = CustomPath(color,mBrushSize)
        mDrawPaint?.color = color
        mDrawPaint?.style = Paint.Style.STROKE
        mDrawPaint?.strokeJoin = Paint.Join.ROUND
        mDrawPaint?.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, wprev: Int, hprev: Int) {
        super.onSizeChanged(w, h, wprev, hprev)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mCanvasBitmap?.let{
            canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint) }

        for (path in mPaths){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path,mDrawPaint!!)
        }

        if (!mDrawpath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawpath!!.brushThickness
            mDrawPaint!!.color = mDrawpath!!.color
            canvas.drawPath(mDrawpath!!,mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x;
        val touchY = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN->{
                mDrawpath!!.color = color
                mDrawpath!!.brushThickness = mBrushSize

                mDrawpath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawpath!!.moveTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE->{
                mDrawpath!!.lineTo(touchX!!,touchY!!)
            }
            MotionEvent.ACTION_UP->{
                mPaths.add(mDrawpath!!)
                mDrawpath = CustomPath(color,mBrushSize)
            }
            else->{return false}
        }
        invalidate()

        return true
    }

    fun setsizeofbrush(newSize : Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setcolor(newColor : String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    internal inner class CustomPath (var color: Int , var brushThickness: Float) : Path(){

    }
}