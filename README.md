# Detection of Diabetic Retinopathy
This is our project for the ZEISS hackathon conducted in January 2018.

This project involves the identification of lesions in the eye to detect diabetic retinopathy by processing its Fundus images. The SIFT algorithm along with techniques such as background subtraction and thresholding were used.

The align.py file aligns two images of a person's eye which are taken before and after injecting a dye like substance. The matching_final.py file finds the co-ordinates of the lesions in the eye.

The co-ordinates are where a doctor must direct laser beams to remove the lesions. This process has been simlated using a stepper motor that moves according to these co-ordinates. The file stepper_zeiss.py does this task.

An Android application was created to provide doctors and patients a UI to view the eye images and the precise location of the lesions.


## Note
The demo Fundus images have been provided to us by ZEISS itself, and we have got the required permission to use them in our project.

### Other Contributors
- Chethan K P - [chethan749](https://github.com/chethan749)
- Rahul Patil - [rahup97](https://github.com/rahup97)
- Rohan Simha - [RohanSimha](https://github.com/RohanSimha)
