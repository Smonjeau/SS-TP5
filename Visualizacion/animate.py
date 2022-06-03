from audioop import add
from tkinter import W
from ovito.io import import_file, export_file
from ovito.modifiers import AffineTransformationModifier,ColorCodingModifier
from ovito.vis import Viewport, PythonViewportOverlay
from ovito.qt_compat import QtCore, QtGui 



static_file = open('static_input.txt', 'r')


width = float(static_file.readline())
height = float(static_file.readline())
groove = float(static_file.readline())
mass = float(static_file.readline())

pared_izq = 322
pared_der =  pared_izq + 155
width_in_pixels = pared_der-pared_izq #width
groove_in_pixels = groove * width_in_pixels / width
alto_pared_pixeles = height * width_in_pixels / width

def add_walls(vp):
    def render_left(args: PythonViewportOverlay.Arguments):
        pen = QtGui.QPen(QtCore.Qt.black, 3, QtCore.Qt.SolidLine)
        args.painter.setPen(pen)
        args.painter.drawLine(pared_izq, 567, pared_izq, 567 - alto_pared_pixeles)

    def render_right(args: PythonViewportOverlay.Arguments):
        pen = QtGui.QPen(QtCore.Qt.black, 3, QtCore.Qt.SolidLine)
        args.painter.setPen(pen)
        args.painter.drawLine(pared_der, 567, pared_der, 567 - alto_pared_pixeles)

    def render_bottom_left(args: PythonViewportOverlay.Arguments):
        pen = QtGui.QPen(QtCore.Qt.black, 3, QtCore.Qt.SolidLine)
        args.painter.setPen(pen)
        args.painter.drawLine(pared_izq, 567, pared_izq+width_in_pixels/2 - groove_in_pixels/2, 567)

    def render_bottom_right(args: PythonViewportOverlay.Arguments):
        pen = QtGui.QPen(QtCore.Qt.black, 3, QtCore.Qt.SolidLine)
        args.painter.setPen(pen)
        args.painter.drawLine(pared_der, 567, pared_der - width_in_pixels/2 + groove_in_pixels/2, 567)

    vp.overlays.append(PythonViewportOverlay(function = render_left))
    vp.overlays.append(PythonViewportOverlay(function = render_right))
    vp.overlays.append(PythonViewportOverlay(function = render_bottom_left))
    vp.overlays.append(PythonViewportOverlay(function = render_bottom_right))
    


    

pipeline = import_file('ovito.xyz', columns = ["Position.X", "Position.Y", "Velocity.X", "Velocity.Y", "Radius"])

pipeline.modifiers.append(AffineTransformationModifier(
    operate_on = {'cell', 'dislocations', 'voxels', 'surfaces'},
    relative_mode = True,
    transformation = [[0, 0, 0, 0],
                          [0, 0, 0, 0],
                          [0, 0, 0, 0]]
))
pipeline.modifiers.append(ColorCodingModifier(
    property = 'Velocity Magnitude',
    gradient = ColorCodingModifier.Hot()
))
pipeline.add_to_scene()
vp = Viewport(type = Viewport.Type.Top)
vp.zoom_all()

add_walls(vp)
#vp.render_image(size=(800,600), filename="image.png")
vp.render_anim(size=(800,600), filename="animation.avi", fps=60, every_nth=50) 