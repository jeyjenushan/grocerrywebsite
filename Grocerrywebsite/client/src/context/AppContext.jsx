import { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { assets, dummyOrders, dummyProducts } from "../greencart_assets/assets";
import toast from "react-hot-toast";
import axios from "axios";

export const AppContext = createContext();

export const AppContextProvider = ({ children }) => {
  const navigate = useNavigate();
  const currency = import.meta.env.VITE_CURRENCY;
  const backEndUrl = import.meta.env.VITE_BACKENDURL;
  const [user, setUser] = useState(null);
  const [isSeller, setIsSeller] = useState(false);

  const [showUserLogin, setShowUserLogin] = useState(false);
  const [products, setProducts] = useState([]);
  const [cartItems, setCartItems] = useState({});
  const [searchQuery, setSearchQuery] = useState({});
  const [myOrders, setMyOrders] = useState([]);
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [address, setAddress] = useState(null);
  const [selectedAddress, setSelectedAddress] = useState(null);
  const [userToken, setUserToken] = useState(
    localStorage.getItem("utoken") || null
  );
  const [sellerToken, setSellerToken] = useState(
    localStorage.getItem("stoken") || null
  );

  // Fetch Seller Status
  const fetchSeller = async () => {
    try {
      const { data } = await axios.get(`${backEndUrl}/api/userDetails`, {
        headers: {
          Authorization: `Bearer ${userToken}`,
        },
      });
      if (data.success) {
        setIsSeller(true);
      } else {
        setIsSeller(false);
      }
    } catch (error) {
      setIsSeller(false);
    }
  };

  // Fetch User Auth Status , User Data and Cart Items
  const fetchUser = async () => {
    try {
      const { data } = await axios.get(`${backEndUrl}/api/userDetails`, {
        headers: {
          Authorization: `Bearer ${sellerToken}`,
        },
      });
      if (data.success) {
        setUser(data.userDto);
        console.log(data);
        setCartItems(data.userDto.cartItems);
      }
    } catch (error) {
      setUser(null);
    }
  };

  const sendOtp = async (email) => {
    try {
      const { data } = await axios.post(
        `${backEndUrl}/api/auth/forgotpassword/send-otp`,
        {},
        {
          params: { email },
        }
      );
      if (data.success) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Failed to send otp your email"); // Display error if doctorDtos is missing
        return false;
      }
    } catch (error) {
      // Log error for debugging and show error message to the user
      console.error(error);
      toast.error(
        error.response?.data?.message ||
          "An error occurred while sending email to the user"
      );
      return false;
    }
  };

  const verifyOtp = async (email, otp) => {
    try {
      const { data } = await axios.post(
        `${backEndUrl}/api/auth/forgotpassword/verify-otp`,
        {},
        {
          params: { email, otp },
        }
      );
      if (data.success) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Wrong OTP provided"); // Display error if doctorDtos is missing
        return false;
      }
    } catch (error) {
      console.error(error);
      toast.error(
        error.response?.data?.message || "An error occurred verify  otp"
      );
      return false;
    }
  };

  const resetPassword = async (email, otp, newPassword) => {
    try {
      const { data } = await axios.post(
        `${backEndUrl}/api/auth/forgotpassword/reset-password`,
        {},
        {
          params: { email, newPassword, otp },
        }
      );
      if (data.success) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Wrong OTP provided");
        return false;
        // Display error if doctorDtos is missing
      }
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || "Error resetting password");
      return false;
    }
  };

  //fetch Orders
  const fetchOrders = async () => {
    try {
      const { data } = await axios.get(`${backEndUrl}/api/orders`, {
        headers: { Authorization: `Bearer ${userToken}` },
      });
      console.log(data);
      if (data.success) {
        setOrders(data.orderDtoList.reverse());
      }
    } catch (error) {
      console.log(error);
    }
  };

  //Fetch All Products
  const fetchProducts = async () => {
    try {
      setIsLoading(true);
      const { data } = await axios.get(`${backEndUrl}/api/product/productList`);
      if (data.success) {
        setProducts(data.productDtoList);
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      toast.error(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  //Add Product to CART
  const addToCart = (itemId) => {
    // 1. Check if user is logged in
    if (!user) {
      toast.error("Please login to add items to cart");
      setShowUserLogin(true); // Show login modal if using
      return;
    }

    // 2. Verify product exists
    const productExists = products.some((product) => product.id === itemId);
    if (!productExists) {
      toast.error("Product not found");
      return;
    }

    // 3. Optimized state update
    setCartItems((prevCartItems) => {
      const newQuantity = (prevCartItems[itemId] || 0) + 1;
      toast.success(`Added to Cart (Total: ${newQuantity})`);

      return {
        ...prevCartItems, // Shallow copy
        [itemId]: newQuantity,
      };
    });
  };

  //update cart item quantity
  const updatedCartItem = (itemId, quantity) => {
    setCartItems((prevCartItems) => ({
      ...prevCartItems, // Copy existing cart items
      [itemId]: quantity, // Update the specific item's quantity
    }));
    toast.success("Cart is updated");
  };

  //Remove from the cart
  const removeFromCart = (itemId) => {
    setCartItems((prevCartItems) => {
      const updatedCart = { ...prevCartItems }; // Shallow copy

      if (updatedCart[itemId] > 1) {
        // Decrease quantity if > 1
        updatedCart[itemId] -= 1;
      } else {
        // Remove item if quantity is 1 (or invalid)
        delete updatedCart[itemId];
      }

      toast.success("Item removed from cart");
      return updatedCart;
    });
  };

  //Get Cart Item Count
  const getCartCount = () => {
    let totalCount = 0;
    for (const item in cartItems) {
      totalCount += cartItems[item];
    }
    return totalCount;
  };

  //Get Cart Total Amount
  const getCartAmount = () => {
    let totalAmount = 0;
    for (const items in cartItems) {
      let itemInfo = products.find((product) => product.id == items);
      if (cartItems[items] > 0) {
        totalAmount += itemInfo.offerPrice * cartItems[items];
      }
    }
    return Math.floor(totalAmount * 100) / 100;
  };

  //verify stripe
  const verifyStripe = async (success, orderId) => {
    try {
      const { data } = await axios.post(
        backEndUrl + "/api/orders/verify",
        { success, orderId },
        { headers: { Authorization: `Bearer ${userToken}` } }
      );

      if (data.success) {
        toast.success(data.message);
        navigate("/my-orders");
      } else {
        toast.error(data.message);
        console.log(data.message);
      }
    } catch (error) {
      toast.error(error.message);
      console.log(error);
    }
  };

  //fetchMyorders
  const fetchMyOrders = async () => {
    try {
      const { data } = await axios.get(`${backEndUrl}/api/orders/user`, {
        headers: {
          Authorization: `Bearer ${userToken}`,
        },
      });
      if (data.success) {
        console.log("jenushan");
        setMyOrders(data.orderDtoList.reverse());
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    fetchUser();
    fetchSeller();
    fetchProducts();
  }, []);

  // Update Database Cart Items

  useEffect(() => {
    const updateCart = async () => {
      try {
        const { data } = await axios.put(
          `${backEndUrl}/api/cart/updateCart`,
          { cartItems }, // Request body as second argument
          {
            // Config object as third argument
            headers: {
              Authorization: `Bearer ${userToken}`,
            },
          }
        );

        if (!data.success) {
          toast.error(data.message);
        }
      } catch (error) {
        toast.error(error.response?.data?.message || error.message);
      }
    };

    if (user) {
      updateCart();
    }
  }, [cartItems, user, userToken]);

  const value = {
    navigate,
    user,
    setUser,
    isSeller,
    setIsSeller,
    setShowUserLogin,
    showUserLogin,
    products,
    currency,
    addToCart,
    updatedCartItem,
    removeFromCart,
    cartItems,
    searchQuery,
    setSearchQuery,
    getCartAmount,
    getCartCount,
    fetchMyOrders,
    setMyOrders,
    myOrders,
    orders,
    setOrders,
    fetchOrders,
    backEndUrl,
    userToken,
    sellerToken,
    setUserToken,
    setSellerToken,
    sendOtp,
    verifyOtp,
    resetPassword,
    setCartItems,
    fetchProducts,
    fetchUser,
    verifyStripe,
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

export const useAppContext = () => {
  return useContext(AppContext);
};
