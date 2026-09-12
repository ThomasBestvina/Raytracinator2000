# The Raytracinator 2000!
Behold the Raytracinator 2000! With this program, scenes can't help but be rendered!

![Image](https://github.com/ThomasBestvina/Raytracinator2000/blob/main/Renders/zonda.png)
![Image](https://github.com/ThomasBestvina/Raytracinator2000/blob/main/Renders/utah_teapot.png)
![Image](https://github.com/ThomasBestvina/Raytracinator2000/blob/main/Renders/spherical_reflections.png)

This is an easily extendable raytracer written in pure java with no external libraries (well except for unit testing). Some examples in its usage are provided.

The raytracer supports the following:
- Arbitrary shapes, including triangles and smooth triangles
- Textures, patterns, and normal maps
- .obj parser, and a json scene parser.
- Multithreading and BVH acceleration structure.
- Extensive test suite. 
- Reasonably fast all things considered.

I intend to expand on this raytracer in the future, however I have focused on correct implementation before large amounts of features.

## Why Java
There are three reasons I chose to use java in spite of it at first seeming to be an unconventional language for a raytracer.

Firstly, a jar with no dependencies will run virtually anywhere. I can raytrace on my smart fridge, or perhaps a smart watch, if I wish. Though I may have to wait a while. 

Secondly, a raytracer made in cpp, rust, or some other fast language is simply not notable. After all, driving a slow car 'fast' is much more fun than driving a fast car fast!

Thirdly, well, I just like java.

## Setup
The project can be pulled and setup as any other java project.
To get the assets for the spherical reflections scene, utah teapot scene, and Pagani Zonda scene, you must install git lfs, then:

`git lfs install`
`git lfs fetch --all`
`git lfs pull`

In order to run:
`./gradlew run --args="path/to/scene.json path/to/output.png"`

## Performance
The "zonda" scene took 4.5 hours with dual 2016 xeon E5-2697A's at 1080p and 16 anti aliasing samples.

## Architecture
Rendering a scene occurs in two stages. 

**Parsing** A scene is described in JSON format (see Examples/), the JSON parser builds a description of the scene in memory. This part also includes parsing relevant obj files into a flat list of triangles, which is then turned into a BVH tree non-lazily.

**Rendering** The camera fires a ray per pixel into the scene, each ray is tested against the root BVHNode, which then recursively asks which shapes have been hit and where, then an intersection result is prepared with the color of the hit, accounting for material, texture, normal maps etc, and how many rays it needs to draw from that point, e.g. in the case of reflection or translucency. 

## License
Copyright (c) 2026 Thomas Bestvina

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
