import React, { useContext, useState } from "react";
import { AppContext } from "../context/AppContext";
import toast from "react-hot-toast";
import { Oval } from "react-loader-spinner";
import { assets } from "../greencart_assets/assets";
import axios from "axios";

const Register = () => {
  const { navigate, backEndUrl } = useContext(AppContext);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
  });
  const [image, setImage] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e) => {
    setImage(e.target.files[0]);
  };

  const onSubmitHandler = async (e) => {
    setLoading(true);
    try {
      e.preventDefault();
      const studentObj = {
        name: formData.name,
        email: formData.email,
        password: formData.password,
      };

      const formDataToSend = new FormData();
      formDataToSend.append("user", JSON.stringify(studentObj));
      if (image) {
        formDataToSend.append("image", image);
      } else {
        toast.error("Image file is required");
        return;
      }
      const { data } = await axios.post(
        `${backEndUrl}/api/auth/register`,
        formDataToSend
      );

      if (data.success) {
        toast.success("The user is registered successfully");
        navigate("/login");
      } else {
        toast.error("The user is not registered successfully");
      }
    } catch (error) {
      console.log(error.message);
      toast.error("The user is not correctly registered and please try again");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-2xl mx-auto p-4 mt-14">
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-semibold text-center mb-6">
          <p>
            <span className="text-primary">User</span>Registration
          </p>
        </h2>

        <form
          onSubmit={onSubmitHandler}
          className="bg-white p-8 rounded-lg shadow-md"
        >
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Left Column */}
            <div className="space-y-4">
              <div>
                <label className="block text-gray-700 mb-1">Full Name</label>
                <input
                  name="name"
                  onChange={handleChange}
                  value={formData.name}
                  className="w-full p-2 border border-primary rounded focus:outline-none focus:ring-2 focus:ring-primary"
                  type="text"
                  required
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Email</label>
                <input
                  name="email"
                  onChange={handleChange}
                  value={formData.email}
                  className="w-full p-2 border border-primary rounded focus:outline-none focus:ring-2 focus:ring-primary"
                  type="email"
                  required
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Password</label>
                <input
                  name="password"
                  onChange={handleChange}
                  value={formData.password}
                  className="w-full p-2 border border-primary rounded focus:outline-none focus:ring-2 focus:ring-primary-500"
                  type="password"
                  required
                />
              </div>
            </div>
          </div>

          {/* Profile Image Upload */}
          <div className="mt-6">
            <label className="block text-gray-700 mb-1">Profile Image</label>
            <div className="flex items-center">
              <label className="cursor-pointer bg-primary hover:bg-gray-200 px-4 py-2 rounded border border-primary">
                <img
                  src={assets.file_upload_icon}
                  alt=""
                  className="p-3 bg-blue-500 rounded"
                />
                <input
                  type="file"
                  id="thumbnailImage"
                  onChange={handleFileChange}
                  accept="image/"
                  hidden
                />
              </label>
              <span className="ml-2 text-gray-500">
                {image ? image.name : "No file chosen"}
              </span>
            </div>
          </div>

          {/* Register Button */}
          <div className="mt-8">
            <button
              type="submit"
              className="bg-primary text-white w-full py-2 rounded-md text-base hover:bg-primary-dull transition-colors flex justify-center items-center"
              disabled={loading}
            >
              {loading ? (
                <Oval
                  height={20}
                  width={20}
                  color="white"
                  visible={true}
                  ariaLabel="oval-loading"
                />
              ) : (
                "Register"
              )}
            </button>
          </div>

          {/* Login Link */}
          <div className="mt-4 text-center">
            <p className="text-gray-600">
              Already have an account?{" "}
              <a href="/login" className="primary hover:underline">
                Login here
              </a>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;
